package projekt.substratum.config;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import static projekt.substratum.config.References.INTERFACER_PACKAGE;

public class ThemeInterfacerService {

    private static final String INTERFACER_TOKEN = "interfacer_token";
    private static final String PRIMARY_COMMAND_KEY = "primary_command_key";
    private static final String JOB_TIME_KEY = "job_time_key";
    private static final String INSTALL_LIST_KEY = "install_list";
    private static final String UNINSTALL_LIST_KEY = "uninstall_list";
    private static final String WITH_RESTART_UI_KEY = "with_restart_ui";
    private static final String BOOTANIMATION_FILE_NAME = "bootanimation_file_name";
    private static final String AUDIO_PID = "audio_pid";
    private static final String AUDIO_FILENAME = "audio_filename";
    private static final String FONTS_FILENAME = "fonts_filename";
    private static final String FONTS_PID = "fonts_pid";
    private static final String ENABLE_LIST_KEY = "enable_list";
    private static final String DISABLE_LIST_KEY = "disable_list";
    private static final String PRIORITY_LIST_KEY = "priority_list";
    private static final String SOURCE_FILE_KEY = "source_file";
    private static final String DESTINATION_FILE_KEY = "destination_file";
    private static final String WITH_DELETE_PARENT_KEY = "with_delete_parent";
    private static final String PROFILE_NAME_KEY = "profile_name";
    private static final String COMMAND_VALUE_INSTALL = "install";
    private static final String COMMAND_VALUE_UNINSTALL = "uninstall";
    private static final String COMMAND_VALUE_RESTART_UI = "restart_ui";
    private static final String COMMAND_VALUE_RESTART_SERVICE = "restart_service";
    private static final String COMMAND_VALUE_CONFIGURATION_SHIM = "configuration_shim";
    private static final String COMMAND_VALUE_BOOTANIMATION = "bootanimation";
    private static final String COMMAND_VALUE_FONTS = "fonts";
    private static final String COMMAND_VALUE_AUDIO = "audio";
    private static final String COMMAND_VALUE_ENABLE = "enable";
    private static final String COMMAND_VALUE_DISABLE = "disable";
    private static final String COMMAND_VALUE_PRIORITY = "priority";
    private static final String COMMAND_VALUE_COPY = "copy";
    private static final String COMMAND_VALUE_MOVE = "move";
    private static final String COMMAND_VALUE_DELETE = "delete";
    private static final String COMMAND_VALUE_PROFILE = "profile";
    private static final String COMMAND_VALUE_MKDIR = "mkdir";

    private static Intent getThemeInterfacer(Context context) {
        Intent intent = new Intent();
        intent.setComponent(
                new ComponentName(
                        INTERFACER_PACKAGE,
                        INTERFACER_PACKAGE + ".services.JobService"));
        PendingIntent pending = PendingIntent.getActivity(context, 0, new Intent(), 0);
        intent.putExtra(INTERFACER_TOKEN, pending);
        intent.putExtra(JOB_TIME_KEY, System.currentTimeMillis());
        return intent;
    }

    @Deprecated
    public static Intent getInterfacer(Context context) {
        Intent intent = new Intent();
        PendingIntent pending = PendingIntent.getActivity(context, 0, new Intent(), 0);
        intent.putExtra(INTERFACER_TOKEN, pending);
        intent.putExtra(JOB_TIME_KEY, System.currentTimeMillis());
        return intent;
    }

    static void installOverlays(Context context, ArrayList<String> overlays) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_INSTALL);
        intent.putExtra(INSTALL_LIST_KEY, overlays);
        context.startService(intent);
    }

    static void uninstallOverlays(Context context, ArrayList<String> overlays, boolean restartUi) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_UNINSTALL);
        intent.putExtra(UNINSTALL_LIST_KEY, overlays);
        // only need to set if true, will restart SystemUI when done processing packages
        intent.putExtra(WITH_RESTART_UI_KEY, restartUi);
        context.startService(intent);
    }

    static void enableOverlays(Context context, ArrayList<String> overlays, boolean restartUi) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_ENABLE);
        intent.putExtra(ENABLE_LIST_KEY, overlays);
        intent.putExtra(WITH_RESTART_UI_KEY, restartUi);
        context.startService(intent);
    }

    static void disableOverlays(Context context, ArrayList<String> overlays, boolean restartUi) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_DISABLE);
        intent.putExtra(DISABLE_LIST_KEY, overlays);
        intent.putExtra(WITH_RESTART_UI_KEY, restartUi);
        context.startService(intent);
    }

    public static void restartSystemUI(Context context) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_RESTART_UI);
        context.startService(intent);
    }

    public static void restartService(Context context) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_RESTART_SERVICE);
        context.startService(intent);
    }

    public static void configurationChangeShim(Context context) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_CONFIGURATION_SHIM);
        context.startService(intent);
    }

    static void setBootAnimation(Context context, String bootanimation_location) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_BOOTANIMATION);
        intent.putExtra(BOOTANIMATION_FILE_NAME, bootanimation_location);
        context.startService(intent);
    }

    static void clearBootAnimation(Context context) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_BOOTANIMATION);
        context.startService((intent));
    }

    static void setFonts(Context context, String pid, String name) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_FONTS);
        intent.putExtra(FONTS_FILENAME, name);
        intent.putExtra(FONTS_PID, pid);
        context.startService(intent);
    }

    static void clearFonts(Context context) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_FONTS);
        context.startService(intent);
    }

    static void setThemedSounds(Context context, String pid, String name) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_AUDIO);
        intent.putExtra(AUDIO_PID, pid);
        intent.putExtra(AUDIO_FILENAME, name);
        context.startService(intent);
    }

    static void clearThemedSounds(Context context) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_AUDIO);
        context.startService(intent);
    }

    static void setPriority(Context context, ArrayList<String> overlays) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_PRIORITY);
        intent.putExtra(PRIORITY_LIST_KEY, overlays);
        context.startService(intent);
    }

    public static void copy(Context context, String source, String destination) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_COPY);
        intent.putExtra(SOURCE_FILE_KEY, source);
        intent.putExtra(DESTINATION_FILE_KEY, destination);
        context.startService(intent);
    }

    public static void move(Context context, String source, String destination) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_MOVE);
        intent.putExtra(SOURCE_FILE_KEY, source);
        intent.putExtra(DESTINATION_FILE_KEY, destination);
        context.startService(intent);
    }

    public static void delete(Context context, String directory, boolean deleteParent) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_DELETE);
        intent.putExtra(SOURCE_FILE_KEY, directory);
        intent.putExtra(WITH_DELETE_PARENT_KEY, deleteParent);
        context.startService(intent);
    }

    public static void applyProfile(Context context, String name, ArrayList<String> toBeDisabled,
                                    ArrayList<String> toBeEnabled, boolean restartUi) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_PROFILE);
        intent.putExtra(PROFILE_NAME_KEY, name);
        intent.putExtra(DISABLE_LIST_KEY, toBeDisabled);
        intent.putExtra(ENABLE_LIST_KEY, toBeEnabled);
        intent.putExtra(WITH_RESTART_UI_KEY, restartUi);
        context.startService(intent);
    }

    public static void createNewFolder(Context context, String destination) {
        Intent intent = getThemeInterfacer(context);
        intent.putExtra(PRIMARY_COMMAND_KEY, COMMAND_VALUE_MKDIR);
        intent.putExtra(DESTINATION_FILE_KEY, destination);
        context.startService(intent);
    }
}
