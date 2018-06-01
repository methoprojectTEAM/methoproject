package com.example.niephox.methophotos.ViewControllers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.example.niephox.methophotos.Controllers.MetadataController;
import com.example.niephox.methophotos.Interfaces.iAsyncCallback;
import com.example.niephox.methophotos.R;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.niephox.methophotos.Controllers.MetadataController.metadataList;

public class MetadataViewController {
    private MetadataController metadataController;
    private ArrayList<String> tagsFiltered = new ArrayList<>();
    public static ArrayList<String> filteredList = new ArrayList<>();

    public MetadataViewController(MetadataController metadataController){
        this.metadataController = metadataController;
    }
    private String[] ReadersList;
    private String[] TagsList;

    public void ReaderAlertDialog(Context context) {
        ReadersList = context.getResources().getStringArray(R.array.readers_list);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Select Reader");
        mBuilder.setSingleChoiceItems(ReadersList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        metadataController.setReaders(null);
                        break;
                    case 1:
                        metadataController.setReaders(Arrays.asList(new ExifReader(), new IptcReader()));
                        break;
                    default:
                        metadataController.setReaders(null);
                        break;
                }
                //metadataController.refreshMetadata();
                metadataController.iAsyncCallback.RefreshView(iAsyncCallback.REQUEST_CODE.METADATA);
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
        filteredList.addAll(metadataController.metadataList);
        if (tagsFiltered.contains("All")) {
            metadataController.filteredList.clear();
            metadataController.filteredList.addAll(filteredList);
            metadataController.iAsyncCallback.RefreshView(iAsyncCallback.REQUEST_CODE.METADATA);
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
            metadataController.filteredList.clear();
            metadataController.filteredList.addAll(filteredList);
            metadataController.iAsyncCallback.RefreshView(iAsyncCallback.REQUEST_CODE.METADATA);
        }
    }


}
