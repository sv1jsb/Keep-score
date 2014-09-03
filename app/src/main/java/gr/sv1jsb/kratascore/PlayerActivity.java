package gr.sv1jsb.kratascore;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gr.sv1jsb.cropimage.CropImage;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;
import gr.sv1jsb.kratascore.persistance.Player;
import gr.sv1jsb.utils.ImageUtils;
import gr.sv1jsb.utils.RandomStringUtils;


public class PlayerActivity extends Activity implements
        PhotoPickDialog.PhotoPickDialogListener,
        DeleteConfirmDialog.DeleteListener,
        AdapterView.OnItemSelectedListener {

    private static final String IMAGE_CHANGED = "changed";
    private static final String IMAGE_NO_CHANGE = "no change";
    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;
    private static final int CROP_PHOTO = 3;
    private static final int REQUEST_SELECT_CONTACT = 4;
    private static final int IMAGE_DIM = android.R.attr.listPreferredItemHeight;
    private static final int IMP_CONTACTS = 0;
    public static final String PHOTO_STATUS = "PHOTO_STATUS";
    public static final String PLAYER_PHOTO = "PLAYER_PHOTO";
    private Player mPlayer;
    private EditText mPlayerName;
    private ImageView mPlayerPhoto;
    private Bitmap mSelectedImage;
    private File EXTERNAL_DIR;
    private File IMAGE_TEMP;
    private int mImportFrom = IMP_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        Intent incoming = getIntent();
        if(incoming.getParcelableExtra(Constants.PLAYER) == null) mPlayer = new Player(0, null, null, null);
        else mPlayer = incoming.getParcelableExtra(Constants.PLAYER);
        EXTERNAL_DIR = getExternalFilesDir(null);

        mPlayerName = (EditText) findViewById(R.id.editPlayerName);
        mPlayerPhoto = (ImageView) findViewById(R.id.ivPlayerPhoto);

        if(savedInstanceState != null){
            mPlayerPhoto.setTag(savedInstanceState.getString(PHOTO_STATUS));
            if(savedInstanceState.getParcelable(PLAYER_PHOTO) != null) {
                mPlayerPhoto.setImageBitmap((Bitmap) savedInstanceState.getParcelable(PLAYER_PHOTO));
                mSelectedImage = savedInstanceState.getParcelable(PLAYER_PHOTO);
            }
        } else {
            mPlayerPhoto.setTag(IMAGE_NO_CHANGE);
            if(mPlayer != null && mPlayer._id != 0) {
                mPlayerName.setText(mPlayer.player);
                if(mPlayer.photo != null && !mPlayer.photo.isEmpty()) {
                    mPlayerPhoto.setImageBitmap(BitmapFactory.decodeFile(EXTERNAL_DIR.getPath() + "/" + mPlayer.photo));
                }
            }
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinnerImportFrom);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.import_from_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        Button mPhotoBtn = (Button) findViewById(R.id.imgbtnUserPhoto);
        mPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerName.post(new Runnable() {
                    @Override
                    public void run() {
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mPlayerName.getWindowToken(), 0);
                    }
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(PhotoPickDialog.TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment photoFrag = PhotoPickDialog.newInstance();
                photoFrag.show(ft, PhotoPickDialog.TAG);
            }
        });
        Button importFromBtn = (Button) findViewById(R.id.ImportFromBtn);
        importFromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImportFrom == IMP_CONTACTS) {
                    contactsIntent();
                }
            }
        });
        Button okBtn = (Button) findViewById(R.id.btnSavePlayer);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPlayerName.getText().toString().equals("")){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag(GenericAlertDialog.TAG);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    String message;
                    message = getResources().getString(R.string.no_player_name);
                    DialogFragment errorFrag = GenericAlertDialog.newInstance(message, null, 0);
                    errorFrag.show(ft, GenericAlertDialog.TAG);
                } else {
                    savePlayer();
                }
            }
        });
        try {
            ActionBar ab = getActionBar();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                ab.setDisplayHomeAsUpEnabled(true);
            if(mPlayer._id == 0) {
                ab.setSubtitle(R.string.new_player_menu);
            } else {
                ab.setSubtitle(R.string.edit_player_sub);
            }

        } catch (NullPointerException e) {
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PHOTO_STATUS, (String) mPlayerPhoto.getTag());
        outState.putParcelable(PLAYER_PHOTO, mSelectedImage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mPlayer._id != 0) {
            getMenuInflater().inflate(R.menu.player_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.delete_player_menu:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(DeleteConfirmDialog.TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                DialogFragment deleteFrag = DeleteConfirmDialog.newInstance(getString(R.string.delete_player_confirmation), mPlayer._id);
                deleteFrag.show(ft, DeleteConfirmDialog.TAG);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    performCrop(data.getDataString(), IMAGE_TEMP.getPath());
                } else {
                    IMAGE_TEMP.delete();
                }
                break;
            case CAPTURE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    if(IMAGE_TEMP != null && IMAGE_TEMP.exists()) {
                        performCrop(IMAGE_TEMP.getPath(), null);
                    }
                } else {
                    IMAGE_TEMP.delete();
                }
                break;
            case CROP_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    performSave(Uri.fromFile(IMAGE_TEMP));
                } else {
                    IMAGE_TEMP.delete();
                }
                break;
            case REQUEST_SELECT_CONTACT:
                if(resultCode == Activity.RESULT_OK){
                    final Uri contactUri = data.getData();
                    importFromContacts(contactUri);
                }
        }
    }

    private void performCrop(String filePath, String savePath){
        Intent cropIntent = new Intent(this, CropImage.class);
        cropIntent.putExtra(CropImage.IMAGE_PATH, filePath);
        if(savePath != null) cropIntent.putExtra(CropImage.SAVE_PATH, savePath);
        cropIntent.putExtra(CropImage.ASPECT_X, 1);
        cropIntent.putExtra(CropImage.ASPECT_Y, 1);
        cropIntent.putExtra(CropImage.OUTPUT_X, 256);
        cropIntent.putExtra(CropImage.OUTPUT_Y, 256);
        if(cropIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(cropIntent, CROP_PHOTO);
        } else {
            performSave(Uri.parse(filePath));
        }
    }

    private void performSave(Uri uri){
        try {
            mSelectedImage = ImageUtils.decodeSampledBitmapFromUri(getContentResolver(), uri, IMAGE_DIM);
            mPlayerPhoto.setImageBitmap(mSelectedImage);
            mPlayerPhoto.setTag(IMAGE_CHANGED);
        } catch (Exception e) {
            mPlayerPhoto.setImageDrawable(getResources().getDrawable(R.drawable.user));
            mPlayerPhoto.setTag(IMAGE_NO_CHANGE);
        } finally {
            IMAGE_TEMP.delete();
        }
    }

    @Override
    public void onPhotoPickChoice(int which) {
        IMAGE_TEMP = new File(EXTERNAL_DIR + "/" + RandomStringUtils.randomAlphanumeric(12));
        if (which == 0){
            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(IMAGE_TEMP));
            if(photoCaptureIntent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(photoCaptureIntent, CAPTURE_PHOTO);
            } else {
                Toast.makeText(this, getString(R.string.no_camera_found), Toast.LENGTH_SHORT).show();
            }
        } else if(which == 1) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
            if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            } else {
                Toast.makeText(this, getString(R.string.no_gallery_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mImportFrom = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        mImportFrom = IMP_CONTACTS;
    }

    private void savePlayer(){
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        String photoName = mPlayer.photo;
        values.put(KrataScoreContract.PlayerEntry.COL_PLAYER, mPlayerName.getText().toString());
        if(((String)mPlayerPhoto.getTag()).equals(IMAGE_CHANGED)){
            if(photoName == null) {
                photoName = RandomStringUtils.randomAlphanumeric(12) + ".jpg";
            }
            values.put(KrataScoreContract.PlayerEntry.COL_PHOTO, photoName);
        }
        Uri returnUri;
        int success;
        if(mPlayer._id == 0) {
            returnUri = cr.insert(KrataScoreContract.PlayerEntry.CONTENT_URI, values);
            success = Integer.parseInt(returnUri.getLastPathSegment());
            mPlayer._id = (long) success;
        } else {
            success = cr.update(Uri.withAppendedPath(KrataScoreContract.PlayerEntry.CONTENT_URI, String.valueOf(mPlayer._id)), values, null, null);
        }
        if(success > 0) {
            if(photoName != null && ((String)mPlayerPhoto.getTag()).equals(IMAGE_CHANGED)) {
                if (!ImageUtils.saveImage(mSelectedImage, EXTERNAL_DIR, photoName)){
                    values.put(KrataScoreContract.PlayerEntry.COL_PHOTO, "");
                    Uri playerUri = Uri.withAppendedPath(KrataScoreContract.PlayerEntry.CONTENT_URI, String.valueOf(mPlayer._id));
                    cr.update(playerUri, values, null, null);
                } else {
                    ImageUtils.updateBitmapToCache(photoName, mSelectedImage);
                }
            }
        }
        setResult(RESULT_OK, new Intent());
        finish();
    }

    private void contactsIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
    }

    private void importFromContacts(Uri contactUri){
        ContentResolver cr = getContentResolver();
        final String[] Projection = {
                ContactsContract.Data._ID,
                ContactsContract.Data.LOOKUP_KEY,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.PHOTO_URI
        };
        final String SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?";
        final String[] SELECTION_ARGS = new String[] {contactUri.getPathSegments().get(2)};
        Cursor c = cr.query(ContactsContract.Data.CONTENT_URI, Projection, SELECTION, SELECTION_ARGS, null);
        if(c.getCount()>0) {
            c.moveToFirst();
            mPlayerName.setText(c.getString(2));
            if(c.getString(3) != null){
                setPlayerImage(Uri.parse(c.getString(3)));
            } else {
                mPlayerPhoto.setImageDrawable(getResources().getDrawable(R.drawable.user));
            }
        }
        c.close();
    }

    private void setPlayerImage(Uri photoUri){
        AssetFileDescriptor afd;
        try {
            afd = getContentResolver().openAssetFileDescriptor(photoUri, "r");
            mSelectedImage = ImageUtils.decodeSampledBitmapFromFD(afd.getFileDescriptor(), IMAGE_DIM);
            mPlayerPhoto.setImageBitmap(mSelectedImage);
            mPlayerPhoto.setTag(IMAGE_CHANGED);
            mPlayer.photo = null;
        } catch (FileNotFoundException e) {
        }

    }

    @Override
    public void onDeletePositiveClick(long playerID) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(KrataScoreContract.ScoreEntry.CONTENT_URI, "player/"+String.valueOf(playerID));
        Cursor cursor = cr.query(uri, KrataScoreContract.ScoreEntry.PROJECTION_JOINED_FULL, null, null, null);
        if(cursor.getCount() == 0) {
            uri = Uri.withAppendedPath(KrataScoreContract.PlayerEntry.CONTENT_URI, String.valueOf(playerID));
            int ok = cr.delete(uri,null,null);
            if(ok == 1) {
                setResult(RESULT_OK, (new Intent()).putExtra(Constants.PLAYER_DELETED, Constants.PLAYER_DELETED_OK));
                finish();
            } else {
                setResult(RESULT_OK, (new Intent()).putExtra(Constants.PLAYER_DELETED, Constants.PLAYER_DELETED_ERROR));
                finish();
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.player_has_games));
            sb.append("\n");
            sb.append(getString(R.string.first_delete_games));
            sb.append("\n");
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                sb.append(cursor.getString(KrataScoreContract.ScoreEntry.NUM_GAME_NAME));
                sb.append(" - ");
                sb.append((new SimpleDateFormat()).format(new Date(Long.parseLong(cursor.getString(KrataScoreContract.ScoreEntry.NUM_GAME_STARTED)))));
                sb.append("\n");
                cursor.moveToNext();
            }
            cursor.close();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(GenericAlertDialog.TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            DialogFragment alertFrag = GenericAlertDialog.newInstance(sb.toString(), null, 0);
            alertFrag.show(ft, GenericAlertDialog.TAG);
        }
    }
}
