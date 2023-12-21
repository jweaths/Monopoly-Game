import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Audio {
    private static boolean soundOn = true;
    public static Clip bgmClip;

    public static void setVolume(Clip clip, float volume) {
        FloatControl theVolume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        theVolume.setValue(20f * (float) Math.log10(volume));
    }

    public static void playAudio(String location) {

        try {
            File BGMPath = new File(location);

            if (BGMPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(BGMPath);

                // loop audio clip if the audio clip to be played is the BGM
                if (location.equals("src/main/resources/bgm.wav")) {
                    bgmClip = AudioSystem.getClip();
                    bgmClip.open(audioInput);
                    bgmClip.start();
                    bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                    setVolume(bgmClip, 0.6f);
                }

                // other audio clips, non looping
                else {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.start();
                }
            }

            else {
                System.out.println("Cannot find location of " + location);
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void toggleSound(Clip clip) {
        if (soundOn) {
            clip.stop();
            soundOn = false;
        }
        else {
            soundOn = true;
            clip.start();
        }
    }

    public static void stopClip(Clip clip) { clip.stop(); }
}
