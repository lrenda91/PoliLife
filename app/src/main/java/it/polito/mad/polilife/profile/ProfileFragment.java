package it.polito.mad.polilife.profile;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mad.polilife.CircularImageView;
import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.chat.CreateChatActivity;
import it.polito.mad.polilife.db.classes.Student;
import it.polito.mad.polilife.db.classes.StudentInfo;
import it.polito.mad.polilife.noticeboard.add.AddNoticeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public interface EditModeChangeListener {
        void onEditModeChanged();
    }

    private Student mUser;
    private StudentInfo mInfo;

    //private ViewGroup contactsLayout, infoLayout;
    private CircularImageView mProfilePhoto;
    private ViewSwitcher[] switchableViews;
    private GridView skillsGrid;


    public static ProfileFragment newInstance(){
        return new ProfileFragment();
    }

    public ProfileFragment() {
        ParseUser user = ParseUser.getCurrentUser();
        mUser = (Student) user;
        mInfo = mUser.getStudentInfo();
    }

    public void switchToEditMode(boolean editMode){
        if (editMode) {

        }
        else {

        }
        for (final ViewSwitcher vs : switchableViews) {
            vs.getCurrentView().animate().alpha(0f).setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            vs.getNextView().setAlpha(0f);
                            vs.showNext();
                            vs.getCurrentView().animate().alpha(1f).setDuration(300).setListener(null).start();
                        }
                    });
        }
        if (getActivity() instanceof EditModeChangeListener){
            ((EditModeChangeListener) getActivity()).onEditModeChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        skillsGrid = (GridView) view.findViewById(R.id.skills);
        mProfilePhoto = (CircularImageView) view.findViewById(R.id.profile_photo);

        if (mUser.getPhoto() != null) {
            try {
                mProfilePhoto.setImageBitmap(Utility.getBitmap(mUser.getPhoto().getData()));
            } catch (Exception e) {
                mProfilePhoto.setImageResource(R.drawable.logo);
            }
        } else {
            mProfilePhoto.setImageResource(R.drawable.logo);
        }
        mProfilePhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                final CharSequence[] items = {"Take Photo", "Choose fillFrom Library",
                        "Cancel"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        } else if (items[item].equals("Choose fillFrom Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select File"), 2);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });

        Resources res = getActivity().getResources();
        DateFormat df = new SimpleDateFormat(res.getString(R.string.date_format));

        //adding contacts info
        String phone = mUser.getContactPhone() != null ?
                mUser.getContactPhone() : res.getString(R.string.no_phone);
        String mail = mUser.getEmail() != null ?
                mUser.getEmail() : res.getString(R.string.no_mail);
        View phoneView = view.findViewById(R.id.phone_item_editable);
        ((TextView) phoneView.findViewById(R.id.item_text)).setText(phone);
        ((EditText) phoneView.findViewById(R.id.item_editText)).setText(phone);
        View mailView = view.findViewById(R.id.mail_item_editable);
        ((TextView) mailView.findViewById(R.id.item_text)).setText(mail);
        ((EditText) mailView.findViewById(R.id.item_editText)).setText(mail);

        //adding basic info
        String name = mUser.getFirstName() != null ?
                mUser.getFirstName() : res.getString(R.string.no_name);
        String surname = mUser.getLastName() != null ?
                mUser.getLastName() : "" ;
        String complete = name + " " + surname;
        View completeNameView = view.findViewById(R.id.name_item_editable);
        ((TextView) completeNameView.findViewById(R.id.item_text)).setText(complete);
        ((EditText) completeNameView.findViewById(R.id.item_editText)).setText(complete);
        String dob = mUser.getBirthDate() != null ?
                df.format(mUser.getBirthDate()) : res.getString(R.string.no_dob);
        View dobView = view.findViewById(R.id.dob_item_editable);
        ((TextView) dobView.findViewById(R.id.item_text)).setText(dob);
        ((EditText) dobView.findViewById(R.id.item_editText)).setText(dob);
        String location =
                mUser.getAddress() != null ? mUser.getAddress() : res.getString(R.string.no_address)
                        + ", " +
                        mUser.getCity() != null ? mUser.getCity() : res.getString(R.string.no_city)
                        + ", " +
                        mUser.getCountry() != null ? mUser.getCountry() : res.getString(R.string.no_country);
        View locationView = view.findViewById(R.id.location_item_editable);
        ((TextView) locationView.findViewById(R.id.item_text)).setText(location);
        ((EditText) locationView.findViewById(R.id.item_editText)).setText(location);
        String aboutMe = mUser.getAbout() != null ? mUser.getAbout() : "";
        View aboutView = view.findViewById(R.id.about_item_editable);
        ((TextView) aboutView.findViewById(R.id.item_text)).setText(aboutMe);
        ((EditText) aboutView.findViewById(R.id.item_editText)).setText(aboutMe);

        switchableViews = new ViewSwitcher[] {
                (ViewSwitcher) phoneView.findViewById(R.id.item_viewSwitcher),
                (ViewSwitcher) mailView.findViewById(R.id.item_viewSwitcher),
                (ViewSwitcher) mailView.findViewById(R.id.item_viewSwitcher),
                (ViewSwitcher) completeNameView.findViewById(R.id.item_viewSwitcher),
                (ViewSwitcher) dobView.findViewById(R.id.item_viewSwitcher),
                (ViewSwitcher) locationView.findViewById(R.id.item_viewSwitcher),
                (ViewSwitcher) aboutView.findViewById(R.id.item_viewSwitcher)
        };

        List<String> skills = mUser.getStudentInfo().getSkills() != null ?
                mUser.getStudentInfo().getSkills() : new ArrayList<String>();
        skillsGrid.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, skills));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddNoticeActivity.RESULT_OK) {
            byte[] byteArray = null;
            if (requestCode == 1) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            else if (requestCode == 2) {
                // Let's read picked image data - its URI
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap thumbnail = BitmapFactory.decodeFile(imagePath, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            mProfilePhoto.setImageBitmap(Utility.getBitmap(byteArray));
        }
    }

}
