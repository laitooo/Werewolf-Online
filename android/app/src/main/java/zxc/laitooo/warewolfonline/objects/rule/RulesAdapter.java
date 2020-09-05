package zxc.laitooo.warewolfonline.objects.rule;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.R;
import zxc.laitooo.warewolfonline.activities.RuleDetailsActivity;

/**
 * Created by Laitooo San on 7/23/2020.
 */

public class RulesAdapter extends RecyclerView.Adapter<RulesHolder> {

    Context c;
    ArrayList<Rule> list;

    public RulesAdapter(Context c, ArrayList<Rule> list) {
        this.c = c;
        this.list = list;
    }

    @Override
    public RulesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RulesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rule,parent,false));
    }

    @Override
    public void onBindViewHolder(RulesHolder holder, int position) {
        final Rule rule = list.get(position);
        holder.name.setText(rule.getName());
        holder.icon.setImageResource(rule.getIcon());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, RuleDetailsActivity.class);
                intent.putExtra("rule",rule.getRole());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
