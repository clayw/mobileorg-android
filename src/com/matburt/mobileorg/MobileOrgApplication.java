package com.matburt.mobileorg;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.util.Log;

public class MobileOrgApplication extends Application {
    public Node rootNode = null;
    public ArrayList<Integer> nodeSelection;
    public ArrayList<EditNode> edits;
    public static final String SYNCHRONIZER_PLUGIN_ACTION = "com.matburt.mobileorg.SYNCHRONIZE";
    private Queue<Timer> activeTimers = new LinkedList<Timer>();
    public void pushSelection(int selectedNode)
    {
        if (nodeSelection == null) {
            nodeSelection = new ArrayList<Integer>();
        }
        nodeSelection.add(new Integer(selectedNode));
    }

    public void popSelection()
    {
        nodeSelection.remove(nodeSelection.size()-1);        
    }
    
    public void clearTimers() {
    	while(!activeTimers.isEmpty()) {
    		activeTimers.poll().cancel();
    	}
    }
    public void addTimer(Timer t) {
    	activeTimers.add(t);
    }

    public Node getSelectedNode()
    {
        Node thisNode = rootNode;
        if (nodeSelection != null) {
            for (int idx = 0; idx < nodeSelection.size(); idx++) {
                thisNode = thisNode.subNodes.get(nodeSelection.get(idx));
            }
        }
        return thisNode;
    }

    static String getStorageFolder()
    {
        File root = Environment.getExternalStorageDirectory();   
        File morgDir = new File(root, "mobileorg");
        return morgDir.getAbsolutePath() + "/";
    }

    static List<PackageItemInfo> discoverSynchronizerPlugins(Context context)
    {
        Intent discoverSynchro = new Intent(SYNCHRONIZER_PLUGIN_ACTION);
        List<ResolveInfo> packages = context.getPackageManager().queryIntentActivities(discoverSynchro,0);
        Log.d("MobileOrg","Found " + packages.size() + " total synchronizer plugins");

        ArrayList<PackageItemInfo> out = new ArrayList<PackageItemInfo>();

        for (ResolveInfo info : packages)
        {
            out.add(info.activityInfo);
            Log.d("MobileOrg","Found synchronizer plugin: "+info.activityInfo.packageName);            
        }
        return out;
    }
}