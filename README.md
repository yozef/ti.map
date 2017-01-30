# Titanium Map Module [![Build Status](https://travis-ci.org/appcelerator-modules/ti.map.svg)](https://travis-ci.org/appcelerator-modules/ti.map)

This is the Map Module for Titanium. Please use [JIRA](http://jira.appcelerator.org) to report issues or ask our [TiSlack community](http://tislack.org) for help! :rocket:

## NOTE About this ti.map branch
- ClickEvent on overlays (Circles, Polygons, etc;) have been removed. The performance on this event looping through a lot of overlays, makes this method useless due to performance issues On Android-only.

## Event Liteners
```javascript
$.mapview.addEventListener('regionchanged', function(e) {
// ANDROID-Only e.done param - when the Map Idles
if (e.done) {
    Ti.API.info("Map RegionChanged END - idle in GMaps SDK");
} else {
   Ti.API.info("Map Region Changing...");
}
});

$.mapview.addEventListener('regionwillchange', function(e) {
   // ANDROID-Only e.reason param
   Ti.API.info("Map Region Will Change..." + (OS_ANDROID?e.reason:null)); // Android - Only
});
```

## Contributors

* Please see https://github.com/appcelerator-modules/ti.map/graphs/contributors
* Interested in contributing? Read the [contributors/committer's](https://wiki.appcelerator.org/display/community/Home) guide.

## Build
1- Import project into Appcelerator Studio:
    import > Existing Folder as New Project
2- Rename build.properties (without .example), and update any info necessary.
3- Make sure your ndk version is at least android-ndk-r10e.
4- Right-click on project > Publish > Package - Android Module

Note: for version api 3, make sure Ti SDK >= 6.x is selected.

## Legal

This module is Copyright (c) 2010-2016 by Appcelerator, Inc. All Rights Reserved. Usage of this module is subject to 
the Terms of Service agreement with Appcelerator, Inc.  
