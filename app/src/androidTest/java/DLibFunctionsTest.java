import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.PeopleDet;
import com.tzutalin.dlib.VisionDetRet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DLibFunctionsTest {

    private Context mInstrumantationCtx;

    @Before
    public void setup() {
        mInstrumantationCtx = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testFacialLandmark() {
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/test.bmp");
        assertThat(bitmap, notNullValue());
        PeopleDet peopleDet = new PeopleDet();
        List<VisionDetRet> results = peopleDet.detBitmapFace(bitmap, Constants.getFaceShapeModelPath());
        for (final VisionDetRet ret : results) {
            String label = ret.getLabel();
            int rectLeft = ret.getLeft();
            int rectTop= ret.getTop();
            int rectRight = ret.getRight();
            int rectBottom = ret.getBottom();
            ArrayList<Point> landmarks = ret.getFaceLandmarks();
            assertThat(label, is("face"));
            assertThat(landmarks.size(), is(68));
        }
    }
}
