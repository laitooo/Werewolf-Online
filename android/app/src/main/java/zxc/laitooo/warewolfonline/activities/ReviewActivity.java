package zxc.laitooo.warewolfonline.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.constants.Integeres;
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 9/5/2020.
 */

public class ReviewActivity extends AppCompatActivity {

    User user;
    RequestQueue requestQueue;

    Spinner spinner;
    EditText editText;
    Button button;
    ImageButton close;

    private final String VERSION_CODE = "0.2.8";
    private final int VERSION = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        user = new UserData(this).getUser();
        requestQueue = Volley.newRequestQueue(this);

        spinner = (Spinner)findViewById(R.id.review);
        editText = (EditText) findViewById(R.id.content);
        close = (ImageButton) findViewById(R.id.finish);
        button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0){
                    submitReview();
                }else {
                    ToastUtils.longToast(ReviewActivity.this, "Please fill the field");
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void submitReview() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Links.ADD_REVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject o = new JSONObject(s);
                            if (o.getBoolean("error")){
                                ToastUtils.networkError(ReviewActivity.this);
                            }else {
                                ToastUtils.longToast(ReviewActivity.this, "Review submitted");
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e("submit review","json: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("submit review","volley: " + volleyError.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("username", user.getUsername());
                map.put("email", user.getEmail());
                map.put("version", "" + VERSION);
                map.put("version_code", VERSION_CODE);
                map.put("type", "" + spinner.getSelectedItemPosition());
                map.put("content", editText.getText().toString());
                return map;
            }
        };
        requestQueue.add(request).setRetryPolicy(new DefaultRetryPolicy(Integeres.TIME_OUT,0,0));
    }
}
