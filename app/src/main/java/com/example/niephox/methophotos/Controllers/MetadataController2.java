package com.example.niephox.methophotos.Controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.example.niephox.methophotos.Activities.AlbumsViewActivity;
import com.example.niephox.methophotos.Entities.Album;
import com.example.niephox.methophotos.Entities.Image;
import com.example.niephox.methophotos.Entities.MetadataTag;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Niephox on 4/20/2018.
 */

public class MetadataController2 extends AsyncTask<Album, Integer, String> implements iAsyncCallback {
    public static ArrayList<String> metadataList = new ArrayList<>();
    public static ArrayList<String> filteredList = new ArrayList<>();
    private ArrayList<String> tagsFiltered = new ArrayList<>();
    private File File;
    Iterable<JpegSegmentMetadataReader> readers = null;
    StorageController storageController = new StorageController();
    public static iAsyncCallback iAsyncCallback;
    private String[] ReadersList;
    private String[] TagsList;
    private Image image;
    public static Album album;

    private AlbumsViewActivity act = new AlbumsViewActivity();

    public MetadataController2(Image image) {
        this.image = image;
        readers = null;
        storageController.registerCallback(this);
    }

    public MetadataController2() {
    }




    public void ExtractMetadata(Image image) {

        if (image.getImageURI() == null) {
            String DownloadURL = image.getDownloadUrl();
            StorageController.DownloadFile(DownloadURL, readers);
        } else {
            File = new File(image.getImageURI());
            DataExtractionFromFile();
        }
    }

    private void DataExtractionFromFile() {
        if (readers == null) {
            ExtractMetadataFromUnknownFileType(File);
        } else {
            ExtractSpecificMetadataType(File, readers);
        }
    }


    private void ExtractMetadataFromUnknownFileType(File file) {

        Metadata metadata = null;
        metadataList.clear();

        try {
            metadata = ImageMetadataReader.readMetadata(File);
            printMetadata(metadata);
        } catch (ImageProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }
        //printMetadata(metadata);
    }


    private void ExtractSpecificMetadataType(File file, Iterable<JpegSegmentMetadataReader> readers) {
        Metadata metadata = null;
        try {
            // SET READERS Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader(), new IptcReader());
            metadata = JpegMetadataReader.readMetadata(file, readers);
            printMetadata(metadata);
        } catch (JpegProcessingException e) {
            print(e);
        } catch (IOException e) {
            print(e);
        }

    }


    public void printMetadata(Metadata metadata) {
        metadataList.clear();
        for (Directory directory : metadata.getDirectories()) {
            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
                MetadataTag metadataTag = new MetadataTag(tag.getTagType(), directory);
                metadataList.add(metadataTag.toString());
            }
            //
            // Each Directory may also contain error messages
            //
            for (String error : directory.getErrors()) {
                System.err.println("ERROR: " + error);
                metadataList.add(error);
            }
        }
        filteredList.addAll(metadataList);
    }

    public void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }

    public void DownloadedFileComplete() {
        iAsyncCallback.RefreshView(REQUEST_CODE.METADATA);
    }

    public void RegisterCallback(iAsyncCallback iAsyncCallback) {
        this.iAsyncCallback = iAsyncCallback;
    }

    @Override
    public void RefreshView(REQUEST_CODE rq) {

    }

    @Override
    public void RetrieveData(REQUEST_CODE rq) {
        switch (rq) {
            case STORAGE:
                File = StorageController.StorageFile;
                DataExtractionFromFile();
                DownloadedFileComplete();
                break;
            default:
                break;
        }
    }

    public void ReaderAlertDialog(Context context) {
        ReadersList = context.getResources().getStringArray(R.array.readers_list);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Select Reader");
        mBuilder.setSingleChoiceItems(ReadersList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        readers = null;
                        break;
                    case 1:
                        readers = Arrays.asList(new ExifReader(), new IptcReader());
                        break;
                    default:
                        readers = null;
                        break;
                }
                ExtractMetadata(image);
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void TagAlertDialog(final Context context) {
        TagsList = context.getResources().getStringArray(R.array.tags_list);
        boolean iCount[] = new boolean[TagsList.length];
        final ArrayList<Integer> selectedList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Tags");
        builder.setMultiChoiceItems(TagsList, iCount, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedList.add(which);
                } else if (selectedList.contains(which)) {
                    selectedList.remove(Integer.valueOf(which));
                }
            }
        }).setCancelable(false)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tagsFiltered.clear();
                        for (int i = 0; i < selectedList.size(); i++) {
                            tagsFiltered.add(TagsList[selectedList.get(i)]);
                        }
                        FilterTagList(tagsFiltered);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Filtering Cancel
                    }
                });
        AlertDialog tagDialog = builder.create();
        tagDialog.show();

    }

    private void FilterTagList(ArrayList<String> tagsFiltered) {
        filteredList.clear();
        filteredList.addAll(metadataList);

        if (tagsFiltered.contains("All")) {
            iAsyncCallback.RefreshView(REQUEST_CODE.METADATA);
        } else {
            boolean removalFlag = false;
            for (int i = filteredList.size() - 1; i >= 0; i--) {
                for (int j = 0; j < tagsFiltered.size(); j++) {
                    removalFlag = false;
                    String[] tagSplit = filteredList.get(i).split("(?<=])", 2);
                    if (tagSplit[0].equals(tagsFiltered.get(j))) {
                        removalFlag = true;
                        break;
                    }
                }
                if (!removalFlag) {
                    filteredList.remove(i);
                }
            }
            iAsyncCallback.RefreshView(REQUEST_CODE.METADATA);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Album... albums) {
        ArrayList<Image> imagesToProcess = albums[0].getImages();
        for (Image image:imagesToProcess) {
            ExtractMetadata(image);
            image.setMetadata(filteredList);
        }
        albums[0].setImages(imagesToProcess);
        album = albums[0];
        iAsyncCallback.RetrieveData(REQUEST_CODE.METADATA);
        return "We processed your album's images";
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

