package it.polito.mad.polilife.didactical.prof;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.DBCallbacks;
import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.Professor;

public class ProfessorsActivity extends AppCompatActivity
        implements DBCallbacks.MultipleFetchCallback<Professor> {

    private Toolbar mToolbar;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professors);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mListView = (ListView) findViewById(R.id.professors_list);

        PoliLifeDB.getAllObjects(Professor.class, this);
    }

    @Override
    public void onFetchSuccess(final List<Professor> result) {
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return result.size();
            }

            @Override
            public Professor getItem(int position) {
                return result.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(ProfessorsActivity.this)
                            .inflate(R.layout.layout_professor_item, parent, false);
                }

                final View details = convertView.findViewById(R.id.prof_details_layout);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (details.getVisibility() == View.GONE) {
                            expand(details, details);
                        } else {
                            collapse(details, details);
                        }
                    }
                });
                final Professor item = getItem(position);
                ((TextView) convertView.findViewById(R.id.professor_name)).setText(item.getName());
                ((TextView) convertView.findViewById(R.id.professor_office)).setText(
                        item.getOffice() + '\n' + item.getOfficeHours());
                final View phone = convertView.findViewById(R.id.professor_phone);
                final View email = convertView.findViewById(R.id.professor_email);
                ((ImageView) phone.findViewById(R.id.rowIcon)).setImageResource(android.R.drawable.ic_menu_call);
                ((TextView) phone.findViewById(R.id.rowText)).setText(item.getPhone());
                ((ImageView) email.findViewById(R.id.rowIcon)).setImageResource(R.drawable.ic_mail);
                ((TextView) email.findViewById(R.id.rowText)).setText(item.getEmail());
                phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + item.getPhone()));
                            startActivity(callIntent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        } catch (SecurityException e) {
                        }
                    }
                });
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{item.getEmail()});
                        email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                        email.putExtra(Intent.EXTRA_TEXT, "");
                        email.setType("message/rfc822");
                        startActivity(Intent.createChooser(email, "Choose an Email client :"));
                    }
                });
                return convertView;
            }
        });
    }

    @Override
    public void onFetchError(Exception exception) {

    }


    private void expand(View view, View parent) {
        //set Visible
        view.setVisibility(View.VISIBLE);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.AT_MOST);
        view.measure(widthSpec, heightSpec);
        int h  = view.getMeasuredHeight();
        Animator mAnimator = slideAnimator(0, h, view);
        mAnimator.start();
    }

    private void collapse(final View view, View parent) {
        int finalHeight = view.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, view);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final View summary) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
