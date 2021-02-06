package com.example.saviak

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.api.model.trip.Trip
import com.squareup.picasso.Picasso

class TripAdapterK(
    context: Context,
    private val layout: Int,
    private val trips: ArrayList<Trip>
) :
    ArrayAdapter<Trip?>(context, layout, trips as List<Trip?>) {
    private val TAG = "HotelAdapter"
    private val inflater: LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v: View = inflater.inflate(R.layout.item_list_trips, null)
        val trip = trips[position]
        val OCL_browser = View.OnClickListener { v ->
            val website = Uri.parse(trips[position]?.url)
            val openWebsite =
                Intent(Intent.ACTION_VIEW, website).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(openWebsite)
            val animScale = AnimationUtils.loadAnimation(
                context, R.anim.anim_scale
            )
            v.startAnimation(animScale)
        }
        val finalConvertView = v
        val OCL_delete = View.OnClickListener {
            val animChildMove = AnimationUtils.loadAnimation(
                context, R.anim.anim_move
            )
            finalConvertView!!.startAnimation(animChildMove)
            animChildMove.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    Log.d("Child", "START")
                }

                override fun onAnimationEnd(animation: Animation) {
                    Log.d("Child", "END")
                    finalConvertView.visibility = View.INVISIBLE
                    Log.d("count child", parent.childCount.toString())
                    Log.d("position", position.toString())
                    trips.remove(trip)
                    notifyDataSetChanged()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        val url: String? = null
        try {
            if (trip!=null)
            Picasso.get().load(trip.getPhoto().thumbnail).resize(parent.width, parent.width)
                .centerCrop()
                .into(v.findViewById<View>(R.id.image_hotel) as ImageView)
        } catch (e: Exception) {
            Log.e(TAG, "Could not load image from: $url")
            //            hotels.remove(hotel);
//            notifyDataSetChanged();
        }
        if (trip!=null) (v.findViewById<View>(R.id.text_item_main) as TextView).text = trip.title
        (v.findViewById<View>(R.id.text_item_main) as TextView).textSize =
            20f
        (v.findViewById<View>(R.id.LL_hotel_delete) as LinearLayout).setOnClickListener(
            OCL_delete
        )
        (v.findViewById<View>(R.id.LL_hotel_browser) as LinearLayout).setOnClickListener(
            OCL_browser
        )
        return v
    } //    public View getView(final int position, final View convertView, ViewGroup parent) {

    //
    //        View view=inflater.inflate(this.layout, parent, false);
    //
    //        final Hotel hotel = hotels.get(position);
    //
    //        ImageView imageHotel = (ImageView) view.findViewById(R.id.image_hotel);
    //        TextView nameView = (TextView) view.findViewById(R.id.text_item_main);
    //        LinearLayout LL_hotel_delete;
    //        LinearLayout LL_hotel_browser;
    //
    //        Button btnDelete=(Button) view.findViewById(R.id.btndelete);
    //        Button btnToBrowser=(Button) view.findViewById(R.id.btntobrower);
    //
    //        View.OnClickListener OCL_browser= new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                Uri website=Uri.parse(hotels.get(position).getUrl());
    //                Intent openWebsite = new Intent(Intent.ACTION_VIEW, website).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //                getContext().startActivity(openWebsite);
    //            }
    //        };
    //        View.OnClickListener OCL_delete= new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                hotels.remove(hotel);
    //                notifyDataSetChanged();
    //            }
    //        };
    //        LL_hotel_browser=view.findViewById(R.id.LL_hotel_browser);
    //        LL_hotel_delete=view.findViewById(R.id.LL_hotel_delete);
    //
    //        LL_hotel_browser.setOnClickListener( OCL_browser);
    //        LL_hotel_delete.setOnClickListener(OCL_delete);
    //        btnDelete.setOnClickListener(OCL_delete);
    //        btnToBrowser.setOnClickListener(OCL_browser);
    //
    //        String url = null;
    //        try {
    //            Picasso.get().load(hotel.getPhotos().getPhoto()).resize(parent.getWidth(), parent.getWidth()).centerCrop().into(imageHotel);
    //        } catch (Exception e) {
    //            Log.e(TAG, "Could not load image from: " + url);
    //            hotels.remove(hotel);
    //            notifyDataSetChanged();
    //        }
    //        nameView.setText(hotel.toString());
    //
    //
    //        return view;
    //    }
    init {
        inflater = LayoutInflater.from(context)
    }
}