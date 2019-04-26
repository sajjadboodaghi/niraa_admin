package ir.sajjadboodaghi.niraa_admin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ir.sajjadboodaghi.niraa_admin.Activities.StoryActivity;
import ir.sajjadboodaghi.niraa_admin.Models.Story;
import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Urls;

/**
 * Created by Sajjad on 09/23/2018.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    Context context;
    List<Story> stories;

    public StoryAdapter(Context context, List<Story> stories) {
        this.context = context;
        this.stories = stories;
    }

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_story_layout, parent, false);
        return new StoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoryAdapter.ViewHolder holder, final int position) {

        final Story story = stories.get(position);
        Picasso.with(context).load(Urls.STORIES_BASE_URL + "small_" + story.getId() + ".jpg")
                .error(ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_image, null))
                .into(holder.storyImageView);

        holder.storyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StoryActivity.class);
                intent.putExtra("storyId", story.getId());
                intent.putExtra("phoneNumber", story.getPhoneNumber());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView storyImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            storyImageView = (ImageView) itemView.findViewById(R.id.storyImageView);
        }
    }
}
