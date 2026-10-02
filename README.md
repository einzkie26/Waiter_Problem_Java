# Waiter Problem Java

A Java Swing simulation of the Waiter Problem. The application sorts plates through prime-number iterations, displays stack movements, compares completed runs in milliseconds, and can export statistics to CSV.

## Requirements

- A **JDK**, not only a JRE, because the project must be compiled with `javac`.
- JDK 8 or newer.
- JDK 17 or newer is recommended for VS Code and current Windows installations.

Check the Java installation in PowerShell:

```powershell
java -version
javac -version
```

Both commands should report the same JDK family. For example, both should start with `17` if JDK 17 is selected.

## Run From PowerShell

Open PowerShell in the project directory:

```powershell
cd D:\project_java\Waiter_Problem_Java
```

Compile all source files into `bin`:

```powershell
javac -d bin src\*.java
```

Start the application:

```powershell
java -cp bin Main
```

The WAV files are stored in `src`. Run the application from the project directory so the sound manager can locate them reliably.

## VS Code JDK Selection

If VS Code uses a different JDK from PowerShell:

1. Open the Command Palette with `Ctrl+Shift+P`.
2. Run **Java: Configure Java Runtime**.
3. Select the same JDK used by `java -version` and `javac -version`.
4. Restart the Java run or debug session.

The project must use a JDK installation that contains both `java.exe` and `javac.exe`.

## Application Workflow

1. Enter positive plate numbers separated by spaces.
2. Enter the number of prime-number iterations.
3. Click **Start**.
4. Watch stack A, stack B, and the activity log.
5. Run the simulation again to compare the two most recent completed runs.
6. Open **Statistics** to view charts or export a CSV file.

The animation interval is fixed at 500 ms so run comparisons use consistent timing.

## Troubleshooting

If `javac` is not recognized, install a JDK and add its `bin` directory to `PATH`.

If `java -version` and `javac -version` report different versions, update `JAVA_HOME` and `PATH`, then reopen PowerShell and VS Code.

If the application compiles but sounds do not play, run it from `D:\project_java\Waiter_Problem_Java` and verify that the WAV files exist in `src`.