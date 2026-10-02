import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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
                URL resource = SoundManager.class.getResource("/" + soundFile);
                if (resource != null) {
                    playClip(AudioSystem.getAudioInputStream(resource));
                    return;
                }

                File[] locations = {
                    new File(soundFile),
                    new File("src", soundFile),
                    new File("bin", soundFile),
                    new File("../src", soundFile),
                    new File("../bin", soundFile)
                };
                for (File file : locations) {
                    if (file.isFile()) {
                        playClip(AudioSystem.getAudioInputStream(file));
                        return;
                    }
                }
                System.err.println("Sound file not found: " + soundFile);
            } catch (Exception e) {
                System.err.println("Sound error: " + e.getMessage());
            }
        }, "waiter-sound").start();
    }

    private static void playClip(AudioInputStream audioStream) throws LineUnavailableException, IOException {
        try (AudioInputStream stream = audioStream) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(stream);
                    clip.start();
        }
    }
    
    private static String getSoundFile(String soundType) {
        switch(soundType) {
            case "start": return "start.wav";
            case "pop": return "pop.wav";
            case "complete": return "complete.wav";
            case "reset": return "reset.wav";
            default: return "pop.wav";
        }
    }
}
