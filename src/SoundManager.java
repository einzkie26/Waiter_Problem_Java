import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    private static boolean soundEnabled = true;
    
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }
    
    public static void playSound(String soundType) {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                String soundFile = getSoundFile(soundType);
                File file = new File(soundFile);
                if (!file.exists()) {
                    file = new File("src/" + soundFile);
                }
                if (!file.exists()) {
                    file = new File("../" + soundFile);
                }
                if (file.exists()) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();
                }
            } catch (Exception e) {
                System.err.println("Sound error: " + e.getMessage());
            }
        }).start();
    }
    
    private static String getSoundFile(String soundType) {
        switch(soundType) {
            case "start": return "start.wav";
            case "pop": return "pop.wav";
            case "push": return "push.wav";
            case "complete": return "complete.wav";
            case "reset": return "reset.wav";
            default: return "pop.wav";
        }
    }
}
