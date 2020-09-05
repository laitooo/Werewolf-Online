package zxc.laitooo.warewolfonline.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class LoginActivity extends AppCompatActivity {

    AppData data;

    Button signIn;
    EditText email,password;
    TextView forgotPassword,signUp;

    Context c;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        data = new AppData(this);
        if (data.getData().isLogged()){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        c = this;


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signIn = (Button)findViewById(R.id.login_button);
        forgotPassword = (TextView)findViewById(R.id.forget_password);
        signUp = (TextView)findViewById(R.id.signup);

        queue = Volley.newRequestQueue(this);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().length() > 0 &&
                        password.getText().toString().length() > 0) {
                    Login();
                }else {
                    ToastUtils.longToast(c,"Please fill all fields");
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void Login(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Links.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.e("JSON RESULT",s);
                        try {
                            JSONObject o = new JSONObject(s);
                            if (!o.getBoolean("error")){
                                JSONObject u = o.getJSONObject("user");
                                User user = new User(u.getInt("id"),email.getText().toString(),
                                        u.getString("username"),password.getText().toString(),
                                        u.getInt("country"),"TOKEN");
                                UserData userData = new UserData(c);
                                userData.saveUser(user);
                                data.setData(new Data(true,false));
                                ToastUtils.longToast(c,"user logged successfully");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                switch (o.getInt("state")){
                                    case 1:
                                        ToastUtils.longToast(c,"Email not found");
                                        break;
                                    case 2:
                                        ToastUtils.longToast(c,"Password not true");
                                        break;
                                    default:
                                        ToastUtils.networkError(c);
                                        break;
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
                map.put("email",email.getText().toString());
                map.put("password",password.getText().toString());
                return map;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(30000,0,0));
    }


}
