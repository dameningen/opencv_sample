package jp.dame.sample;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class OpenCVSampleTest {

    @Nested
    class animeFaceDetect {
        @Test
        @DisplayName("正常系01")
        void success() {
            OpenCVSample target = new OpenCVSample();
            try {
                target.animeFaceDetect("src/test/resources/target_imgs");
            } catch (Exception e) {
                fail("例外が発生しました。", e);
            }
        }

    }

}
