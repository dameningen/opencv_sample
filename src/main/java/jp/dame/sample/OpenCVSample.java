package jp.dame.sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * アニメ顔画像分類処理サンプル実装
 *
 * @author dameningen
 *
 */
public class OpenCVSample {

    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenCVSample.class);

    /**
     * リソースファイル配置ディレクトリパス
     */
    private static final String RESOUCE_DIR = "src/main/resources";

    /**
     * 結果ファイル出力ディレクトリ
     */
    private static final String OUTPUT_DIR = "output";

    /**
     * 一致ファイル出力ディレクトリ
     */
    private static final String MATCH_OUTPUT_DIR = OUTPUT_DIR + "/match";

    /**
     * 不一致ファイル出力ディレクトリ
     */
    private static final String UNMATCH_OUTPUT_DIR = OUTPUT_DIR + "/unmatch";

    /**
     * アニメ顔分類器ファイル名
     */
    private static final String CASCADE_CLASSIFIER_FILE_NAME = RESOUCE_DIR
            + "/data/lbpcascade_animeface.xml";

    // システムライブラリロード
    static {
        // OpenCVのネイティブライブラリ読み込み（プロジェクト直下のnatvieディレクトリにdll/soを配置しておく）
        System.loadLibrary("native/" + Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * パラメータに設定された分類対象画像格納パス配下の画像にアニメ顔が
     * 含まれるかどうかを判定し、振り分ける処理を実行する。
     * （分類処理の際アニメ顔部分を矩形で囲む。）
     *
     * @param srcDirPath 分類対象画像格納パス
     */
    public void animeFaceDetect(String srcDirPath) throws IOException {
        LOGGER.debug("■開始");

        try {
            // アニメ顔分類器をLoadする
            CascadeClassifier animeFaceDetector = new CascadeClassifier(CASCADE_CLASSIFIER_FILE_NAME);
            if (animeFaceDetector.empty()) {
                // 物体検出用設定ファイルが存在しない場合は例外をthrowする
                throw new FileNotFoundException("設定ファイルが存在していません。[" + CASCADE_CLASSIFIER_FILE_NAME + "]");
            }

            // アニメ顔認識処理用インスタンス生成
            MatOfRect faceDetections = new MatOfRect();

            // 読み込み対象ディレクトリ配下のファイル一覧取得
            List<Path> files = Files.list(Paths.get(srcDirPath)).collect(Collectors.toList());
            LOGGER.debug("■処理対象ファイル数：" + files.size());

            for (Path path : files) {
                String infFileName = path.toString();
                String outFileName = "";
                LOGGER.debug(infFileName + "の処理開始。");

                Mat image = Imgcodecs.imread(infFileName);
                animeFaceDetector.detectMultiScale(image, faceDetections);

                // 見つかった顔を矩形で囲む
                for (Rect rect : faceDetections.toArray()) {
                    Imgproc.rectangle(image,
                            new Point(rect.x, rect.y),
                            new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(255, 0, 255),
                            5);
                }

                if (!faceDetections.empty()) {
                    LOGGER.debug(String.format("★認識された顔の数：%s", faceDetections.toArray().length));
                    // 出力ファイル名設定
                    outFileName = MATCH_OUTPUT_DIR + "/" + path.getFileName();
                } else {
                    // 出力ファイル名設定
                    outFileName = UNMATCH_OUTPUT_DIR + "/" + path.getFileName();
                }
                // ファイルを書き出す
                Imgcodecs.imwrite(outFileName, image);

            }
            LOGGER.debug("■終了");

        } catch (IOException e) {
            LOGGER.error("例外発生", e);
            throw e;
        }

    }

}
