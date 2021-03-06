package projekt.substratum.tabs;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import projekt.substratum.InformationActivity;
import projekt.substratum.R;
import projekt.substratum.adapters.SoundsAdapter;
import projekt.substratum.config.FileOperations;
import projekt.substratum.config.References;
import projekt.substratum.model.SoundsInfo;
import projekt.substratum.util.RecyclerItemClickListener;
import projekt.substratum.util.SoundUtils;

import static projekt.substratum.util.SoundUtils.finishReceiver;

public class Sounds extends Fragment {

    private String theme_pid;
    private ViewGroup root;
    private MaterialProgressBar progressBar;
    private ImageButton imageButton;
    private Spinner soundsSelector;
    private ColorStateList unchecked, checked;
    private ArrayList<SoundsInfo> wordList;
    private RecyclerView recyclerView;
    private MediaPlayer mp = new MediaPlayer();
    private int previous_position;
    private RelativeLayout relativeLayout, error;
    private RelativeLayout defaults;
    private ProgressDialog mProgressDialog;
    private SharedPreferences prefs;
    private AsyncTask current;
    private NestedScrollView nsv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        theme_pid = InformationActivity.getThemePID();

        root = (ViewGroup) inflater.inflate(R.layout.tab_fragment_4, container, false);
        nsv = (NestedScrollView) root.findViewById(R.id.nestedScrollView);

        progressBar = (MaterialProgressBar) root.findViewById(R.id.progress_bar_loader);
        progressBar.setVisibility(View.GONE);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        defaults = (RelativeLayout) root.findViewById(R.id.restore_to_default);

        imageButton = (ImageButton) root.findViewById(R.id.checkBox);
        imageButton.setClickable(false);
        imageButton.setOnClickListener(v -> {
            if (soundsSelector.getSelectedItemPosition() == 1) {
                new SoundsClearer().execute("");
            } else {
                new SoundUtils().execute(nsv,
                        soundsSelector.getSelectedItem().toString(), getContext(), theme_pid);
            }
        });

        final RelativeLayout sounds_preview =
                (RelativeLayout) root.findViewById(R.id.sounds_placeholder);
        relativeLayout = (RelativeLayout) root.findViewById(R.id.relativeLayout);
        error = (RelativeLayout) root.findViewById(R.id.error_loading_pack);
        error.setVisibility(View.GONE);

        // Pre-initialize the adapter first so that it won't complain for skipping layout on logs
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<SoundsInfo> empty_array = new ArrayList<>();
        RecyclerView.Adapter empty_adapter = new SoundsAdapter(empty_array);
        recyclerView.setAdapter(empty_adapter);

        unchecked = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        getContext().getColor(R.color.sounds_unchecked),
                        getContext().getColor(R.color.sounds_unchecked)
                }
        );
        checked = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        getContext().getColor(R.color.sounds_checked),
                        getContext().getColor(R.color.sounds_checked)
                }
        );

        try {
            File f =
                    new File(
                            getContext().getCacheDir().getAbsoluteFile() + "/SubstratumBuilder/" +
                                    theme_pid + "/assets/audio");
            File[] fileArray = f.listFiles();
            ArrayList<String> archivedSounds = new ArrayList<>();
            for (File file : fileArray) {
                archivedSounds.add(file.getName());
            }
            ArrayList<String> unarchivedSounds = new ArrayList<>();
            unarchivedSounds.add(getString(R.string.sounds_default_spinner));
            unarchivedSounds.add(getString(R.string.sounds_spinner_set_defaults));
            for (int i = 0; i < archivedSounds.size(); i++) {
                unarchivedSounds.add(archivedSounds.get(i).substring(0,
                        archivedSounds.get(i).length() - 4));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, unarchivedSounds);
            soundsSelector = (Spinner) root.findViewById(R.id.soundsSelection);
            soundsSelector.setAdapter(adapter1);
            soundsSelector.setOnItemSelectedListener(new AdapterView
                    .OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
                    switch (pos) {
                        case 0:
                            if (current != null)
                                current.cancel(true);
                            defaults.setVisibility(View.GONE);
                            imageButton.setClickable(false);
                            imageButton.setImageTintList(unchecked);
                            error.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.GONE);
                            sounds_preview.setVisibility(View.VISIBLE);
                            break;

                        case 1:
                            if (current != null)
                                current.cancel(true);
                            defaults.setVisibility(View.VISIBLE);
                            imageButton.setImageTintList(checked);
                            imageButton.setClickable(true);
                            error.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.GONE);
                            sounds_preview.setVisibility(View.GONE);
                            break;

                        default:
                            if (current != null)
                                current.cancel(true);
                            defaults.setVisibility(View.GONE);
                            error.setVisibility(View.GONE);
                            sounds_preview.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.VISIBLE);
                            String[] commands = {arg0.getSelectedItem().toString()};
                            current = new SoundsPreview().execute(commands);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SoundUtils",
                    "There is no sounds.zip found within the assets of this theme!");
        }

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), (view, position) -> {
                    wordList.get(position);
                    try {
                        if (!mp.isPlaying() || position != previous_position) {
                            stopPlayer();
                            ((ImageButton) view.findViewById(R.id.play))
                                    .setImageResource(R.drawable.sounds_preview_stop);
                            mp.setDataSource(wordList.get(position).getAbsolutePath());
                            mp.prepare();
                            mp.start();
                        } else {
                            stopPlayer();
                        }
                        previous_position = position;
                    } catch (IOException ioe) {
                        Log.e("SoundUtils", "Playback has failed for " + wordList.get
                                (position).getTitle());
                    }
                })
        );
        mp.setOnCompletionListener(mediaPlayer -> stopPlayer());
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();

        // Unregister finish receiver
        try {
            if (finishReceiver != null)
                getContext().unregisterReceiver(finishReceiver);
        } catch (IllegalArgumentException e) {
            // unregistered already
        }
    }

    private void stopPlayer() {
        final int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((ImageButton) recyclerView.getChildAt(i).findViewById(R.id.play))
                    .setImageResource(R.drawable.sounds_preview_play);
        }
        mp.reset();
    }

    private class SoundsClearer extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getActivity(), R.style.RestoreDialog);
            mProgressDialog.setMessage(getString(R.string.manage_dialog_performing));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("sounds_applied");
            editor.apply();
            Snackbar.make(nsv,
                    getString(R.string.manage_sounds_toast),
                    Snackbar.LENGTH_LONG)
                    .show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            new SoundUtils().SoundsClearer(getContext());
            return null;
        }
    }

    private class SoundsPreview extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            imageButton.setClickable(false);
            imageButton.setImageTintList(unchecked);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                List<SoundsInfo> adapter1 = new ArrayList<>(wordList);

                if (adapter1.size() > 0) {
                    recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
                    SoundsAdapter mAdapter = new SoundsAdapter(adapter1);
                    RecyclerView.LayoutManager mLayoutManager =
                            new LinearLayoutManager(getContext());

                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);

                    imageButton.setImageTintList(checked);
                    imageButton.setClickable(true);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("SoundUtils",
                        "Window was destroyed before AsyncTask could perform postExecute()");
            }
        }

        @Override
        protected String doInBackground(String... sUrl) {
            try {
                File cacheDirectory = new File(getContext().getCacheDir(), "/SoundsCache/");
                if (!cacheDirectory.exists()) {
                    boolean created = cacheDirectory.mkdirs();
                    if (created)
                        Log.d("SoundUtils", "Sounds folder created");
                }
                File cacheDirectory2 = new File(getContext().getCacheDir(), "/SoundCache/" +
                        "sounds_preview/");
                if (!cacheDirectory2.exists()) {
                    boolean created = cacheDirectory2.mkdirs();
                    if (created)
                        Log.d("SoundUtils", "Sounds work folder created");
                } else {
                    FileOperations.delete(getContext(), getContext().getCacheDir()
                            .getAbsolutePath() +
                            "/SoundsCache/sounds_preview/");
                    boolean created = cacheDirectory2.mkdirs();
                    if (created)
                        Log.d("SoundUtils", "Sounds folder recreated");
                }

                // Copy the sounds.zip from assets/sounds of the theme's assets

                String source = sUrl[0] + ".zip";

                try {
                    File f = new File(getContext().getCacheDir().getAbsoluteFile() +
                            "/SubstratumBuilder/" + theme_pid + "/assets/audio/" + source);
                    try (InputStream inputStream = new FileInputStream(f);
                         OutputStream outputStream = new FileOutputStream(getContext().getCacheDir()
                                 .getAbsolutePath() + "/SoundsCache/" + source)) {
                        CopyStream(inputStream, outputStream);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("SoundUtils",
                            "There is no sounds.zip found within the assets of this theme!");
                }

                // Unzip the sounds archive to get it prepared for the preview
                unzip(getContext().getCacheDir().getAbsolutePath() + "/SoundsCache/" + source,
                        getContext().getCacheDir().getAbsolutePath() +
                                "/SoundsCache/sounds_preview/");

                wordList = new ArrayList<>();
                File testDirectory =
                        new File(getContext().getCacheDir().getAbsolutePath() +
                                "/SoundsCache/sounds_preview/");
                listFilesForFolder(testDirectory);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SoundUtils", "Unexpectedly lost connection to the application host");
            }
            return null;
        }

        void listFilesForFolder(final File folder) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry);
                } else {
                    if (!fileEntry.getName().substring(0, 1).equals(".") &&
                            References.allowedSounds(fileEntry.getName())) {
                        wordList.add(new SoundsInfo(getContext(), fileEntry.getName(),
                                fileEntry.getAbsolutePath()));
                    }
                }
            }
        }

        private void unzip(String source, String destination) {
            try (ZipInputStream inputStream =
                         new ZipInputStream(new BufferedInputStream(new FileInputStream(source)))) {
                ZipEntry zipEntry;
                int count;
                byte[] buffer = new byte[8192];
                while ((zipEntry = inputStream.getNextEntry()) != null) {
                    File file = new File(destination, zipEntry.getName());
                    File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (zipEntry.isDirectory())
                        continue;
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        while ((count = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, count);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SoundUtils",
                        "An issue has occurred while attempting to decompress this archive.");
            }
        }

        private void CopyStream(InputStream Input, OutputStream Output) throws IOException {
            byte[] buffer = new byte[5120];
            int length = Input.read(buffer);
            while (length > 0) {
                Output.write(buffer, 0, length);
                length = Input.read(buffer);
            }
        }
    }
}