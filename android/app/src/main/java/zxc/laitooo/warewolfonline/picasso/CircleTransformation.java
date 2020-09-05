package zxc.laitooo.warewolfonline.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.squareup.picasso.Transformation;

/**
 * Created by Laitooo San on 5/9/2020.
 */

public class CircleTransformation implements Transformation {

    @Override

    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size);
        int y = (source.getHeight() - size);
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);

        if (squaredBitmap != source) {
            source.recycle();
        }
        Log.d("Values","Values"+" X "+x+ " Y "+y+" Size "+ size + " Width "+ source.getWidth() + " Height " + source.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.MIRROR, BitmapShader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setAntiAlias(false);
        float r = size / 2f;
        Log.d("Values","Values"+" R "+r);

        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}

