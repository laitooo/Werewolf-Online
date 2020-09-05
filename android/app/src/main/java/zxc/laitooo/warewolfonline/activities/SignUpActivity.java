package zxc.laitooo.warewolfonline.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.user.AppData;
import zxc.laitooo.warewolfonline.user.Data;
import zxc.laitooo.warewolfonline.user.User;
import zxc.laitooo.warewolfonline.user.UserData;
import zxc.laitooo.warewolfonline.utils.ToastUtils;

/**
 * Created by Laitooo San on 2/28/2020.
 */

public class SignUpActivity extends AppCompatActivity {

    AppData data;

    EditText email,password,username;
    Spinner country;

    Context c;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        c = this;

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        country = (Spinner) findViewById(R.id.country);
        Button signUp = (Button) findViewById(R.id.signup_button);

        queue = Volley.newRequestQueue(this);
        data = new AppData(c);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_text,
                R.id.spinner_textview,getResources().getStringArray(R.array.countries_array));
        country.setAdapter(adapter);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().length() > 0 &&
                        password.getText().toString().length() > 0 &&
                        username.getText().toString().length() > 0 &&
                        country.getSelectedItemPosition() > 0) {
                    Register();
                }else {
                    ToastUtils.longToast(c,"Please fill all fields");
                }
            }
        });
    }

    public void Register(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Links.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject o = new JSONObject(s);
                            if (!o.getBoolean("error")){
                                User user = new User(o.getInt("id"),email.getText().toString(),
                                        username.getText().toString(),password.getText().toString(),
                                        country.getSelectedItemPosition(),"TOKEN");
                                Log.e("Sign up","pic: " + o.getString("pic"));
                                UserData userData = new UserData(c);
                                userData.saveUser(user);
                                data.setData(new Data(true,false));
                                ToastUtils.longToast(c,"user created successfully");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                if (o.getInt("state") == 1) {
                                    ToastUtils.longToast(c,"You already have account");
                                }else {
                                    ToastUtils.networkError(c);
                                }
                            }
                        } catch (JSONException e) {
                            ToastUtils.networkError(c);
                            Log.e("Login","json:" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtils.networkError(c);
                        Log.e("Login","volley:" + volleyError.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("username",username.getText().toString());
                map.put("email",email.getText().toString());
                map.put("password",password.getText().toString());
                map.put("country",String.valueOf(country.getSelectedItemPosition()+1));
                map.put("token","token1");
                return map;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(30000,0,0));
    }

}