/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

import ti.map.AnnotationProxy.AnnotationDelegate;
import android.app.Activity;
import android.os.Message;

@Kroll.proxy(creatableInModule = MapModule.class, propertyAccessors = {
		TiC.PROPERTY_USER_LOCATION, MapModule.PROPERTY_USER_LOCATION_BUTTON,
		TiC.PROPERTY_MAP_TYPE, TiC.PROPERTY_REGION, TiC.PROPERTY_ANNOTATIONS,
		TiC.PROPERTY_ANIMATE, MapModule.PROPERTY_TRAFFIC, TiC.PROPERTY_STYLE,
		TiC.PROPERTY_ENABLE_ZOOM_CONTROLS, MapModule.PROPERTY_COMPASS_ENABLED  })
public class ViewProxy extends TiViewProxy implements AnnotationDelegate {
	private static final String TAG = "MapViewProxy";

	private static final int MSG_FIRST_ID = TiViewProxy.MSG_LAST_ID + 1;

	private static final int MSG_ADD_ANNOTATION = MSG_FIRST_ID + 500;
	private static final int MSG_ADD_ANNOTATIONS = MSG_FIRST_ID + 501;
	private static final int MSG_REMOVE_ANNOTATION = MSG_FIRST_ID + 502;
	private static final int MSG_REMOVE_ANNOTATIONS = MSG_FIRST_ID + 503;
	private static final int MSG_REMOVE_ALL_ANNOTATIONS = MSG_FIRST_ID + 504;
	private static final int MSG_SELECT_ANNOTATION = MSG_FIRST_ID + 505;
	private static final int MSG_DESELECT_ANNOTATION = MSG_FIRST_ID + 506;
	private static final int MSG_ADD_ROUTE = MSG_FIRST_ID + 507;
	private static final int MSG_REMOVE_ROUTE = MSG_FIRST_ID + 508;
	private static final int MSG_CHANGE_ZOOM = MSG_FIRST_ID + 509;
	private static final int MSG_SET_LOCATION = MSG_FIRST_ID + 510;
	private static final int MSG_MAX_ZOOM = MSG_FIRST_ID + 511;
	private static final int MSG_MIN_ZOOM = MSG_FIRST_ID + 512;
	private static final int MSG_SNAP_SHOT = MSG_FIRST_ID + 513;

	private static final int MSG_ADD_POLYGON = MSG_FIRST_ID + 901;
	private static final int MSG_REMOVE_POLYGON = MSG_FIRST_ID + 902;
	private static final int MSG_REMOVE_ALL_POLYGONS = MSG_FIRST_ID + 903;
	
	private static final int MSG_ADD_POLYLINE = MSG_FIRST_ID + 910;
	private static final int MSG_REMOVE_POLYLINE = MSG_FIRST_ID + 911;
	private static final int MSG_REMOVE_ALL_POLYLINES = MSG_FIRST_ID + 912;
	
	private static final int MSG_ADD_CIRCLE = MSG_FIRST_ID + 921;
	private static final int MSG_REMOVE_CIRCLE = MSG_FIRST_ID + 922;
	private static final int MSG_REMOVE_ALL_CIRCLES = MSG_FIRST_ID + 923;

	private final ArrayList<RouteProxy> preloadRoutes;
	private final ArrayList<PolygonProxy> preloadPolygons;
	private final ArrayList<PolylineProxy> preloadPolylines;
	private final ArrayList<CircleProxy> preloadCircles;

	public ViewProxy() {
		super();
		preloadRoutes = new ArrayList<RouteProxy>();
		defaultValues.put(MapModule.PROPERTY_COMPASS_ENABLED, true);
		preloadPolygons = new ArrayList<PolygonProxy>();
		preloadPolylines = new ArrayList<PolylineProxy>();
		preloadCircles = new ArrayList<CircleProxy>();
	}

	@Override
	public TiUIView createView(Activity activity) {
		return new TiUIMapView(this, activity);
	}

	public void clearPreloadObjects() {
		preloadRoutes.clear();
		preloadPolygons.clear();
		preloadPolylines.clear();
		preloadCircles.clear();
	}

	@Override
	public boolean handleMessage(Message msg) {
		AsyncResult result = null;
		switch (msg.what) {

		case MSG_ADD_ANNOTATION: {
			result = (AsyncResult) msg.obj;
			handleAddAnnotation((AnnotationProxy) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_ADD_ANNOTATIONS: {
			result = (AsyncResult) msg.obj;
			handleAddAnnotations((Object[]) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_ANNOTATION: {
			result = (AsyncResult) msg.obj;
			handleRemoveAnnotation(result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_ANNOTATIONS: {
			result = (AsyncResult) msg.obj;
			handleRemoveAnnotations((Object[]) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_ALL_ANNOTATIONS: {
			result = (AsyncResult) msg.obj;
			handleRemoveAllAnnotations();
			result.setResult(null);
			return true;
		}

		case MSG_SELECT_ANNOTATION: {
			result = (AsyncResult) msg.obj;
			handleSelectAnnotation(result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_DESELECT_ANNOTATION: {
			result = (AsyncResult) msg.obj;
			handleDeselectAnnotation(result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_ADD_ROUTE: {
			result = (AsyncResult) msg.obj;
			handleAddRoute(result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_ROUTE: {
			result = (AsyncResult) msg.obj;
			handleRemoveRoute((RouteProxy) result.getArg());
			result.setResult(null);
			return true;
		}
		
		case MSG_MAX_ZOOM: {
			result = (AsyncResult) msg.obj;
			result.setResult(getMaxZoom());
			return true;
		}
		
		case MSG_MIN_ZOOM: {
			result = (AsyncResult) msg.obj;
			result.setResult(getMinZoom());
			return true;
		}

		case MSG_CHANGE_ZOOM: {
			handleZoom(msg.arg1);
			return true;
		}

		case MSG_SET_LOCATION: {
			handleSetLocation((HashMap) msg.obj);
			return true;
		}

		case MSG_SNAP_SHOT: {
			handleSnapshot();
			return true;
		}

		case MSG_ADD_POLYGON: {
			result = (AsyncResult) msg.obj;
			handleAddPolygon(result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_POLYGON: {
			result = (AsyncResult) msg.obj;
			handleRemovePolygon((PolygonProxy) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_ALL_POLYGONS: {
			result = (AsyncResult) msg.obj;
			handleRemoveAllPolygons();
			result.setResult(null);
			return true;
		}
		
		case MSG_ADD_POLYLINE: {
			result = (AsyncResult) msg.obj;
			handleAddPolyline(result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_POLYLINE: {
			result = (AsyncResult) msg.obj;
			handleRemovePolyline((PolylineProxy) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_ALL_POLYLINES: {
			result = (AsyncResult) msg.obj;
			handleRemoveAllPolylines();
			result.setResult(null);
			return true;
		}
		
		case MSG_ADD_CIRCLE: {
			result = (AsyncResult) msg.obj;
			handleAddCircle((CircleProxy) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_CIRCLE: {
			result = (AsyncResult) msg.obj;
			handleRemoveCircle((CircleProxy) result.getArg());
			result.setResult(null);
			return true;
		}

		case MSG_REMOVE_ALL_CIRCLES: {
			result = (AsyncResult) msg.obj;
			handleRemoveAllCircles();
			result.setResult(null);
			return true;
		}

		default: {
			return super.handleMessage(msg);
		}
		}
	}

	@Kroll.method
	public void addAnnotation(AnnotationProxy annotation) {
		// Update the JS object
		Object annotations = getProperty(TiC.PROPERTY_ANNOTATIONS);
		if (annotations instanceof Object[]) {
			ArrayList<Object> annoList = new ArrayList<Object>(
					Arrays.asList((Object[]) annotations));
			annoList.add(annotation);
			setProperty(TiC.PROPERTY_ANNOTATIONS, annoList.toArray());
		} else {
			setProperty(TiC.PROPERTY_ANNOTATIONS, new Object[] { annotation });
		}
		annotation.setDelegate(this);
		
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			if (TiApplication.isUIThread()) {
				handleAddAnnotation(annotation);
			} else {
				TiMessenger.sendBlockingMainMessage(getMainHandler()
						.obtainMessage(MSG_ADD_ANNOTATION), annotation);
			}
		}
	}

	private void handleAddAnnotation(AnnotationProxy annotation) {
		TiUIMapView mapView = (TiUIMapView) peekView();
		if (mapView.getMap() != null) {
			mapView.addAnnotation(annotation);
		}
	}

	@Kroll.method
	public void addAnnotations(Object annoObject) {
		if (!(annoObject instanceof Object[])) {
			Log.e(TAG, "Invalid argument to addAnnotations",  Log.DEBUG_MODE);
			return;
		}
		Object[] annos = (Object[])annoObject;

		//Update the JS object
		Object annotations = getProperty(TiC.PROPERTY_ANNOTATIONS);
		if (annotations instanceof Object[]) {
			ArrayList<Object> annoList = new ArrayList<Object>(
					Arrays.asList((Object[]) annotations));
			for (int i = 0; i < annos.length; i++) {
				Object annotationObject = annos[i];
				if (annotationObject instanceof AnnotationProxy) {
					annoList.add(annotationObject);
				}
			}
			setProperty(TiC.PROPERTY_ANNOTATIONS, annoList.toArray());
		} else {
			setProperty(TiC.PROPERTY_ANNOTATIONS, annos);
		}

		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			if (TiApplication.isUIThread()) {
				handleAddAnnotations(annos);
			} else {
				TiMessenger.sendBlockingMainMessage(getMainHandler()
						.obtainMessage(MSG_ADD_ANNOTATIONS), annos);
			}
		}
	}

	private void handleAddAnnotations(Object[] annotations) {
		for (int i = 0; i < annotations.length; i++) {
			Object annotation = annotations[i];
			if (annotation instanceof AnnotationProxy) {
				handleAddAnnotation((AnnotationProxy) annotation);
			}
		}
	}

	@Kroll.method
	public void snapshot() {
		if (TiApplication.isUIThread()) {
			handleSnapshot();
		} else {
			getMainHandler().obtainMessage(MSG_SNAP_SHOT).sendToTarget();
		}
	}

	private void handleSnapshot() {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			((TiUIMapView) view).snapshot();
		}
	}

	@Kroll.method
	public void removeAllAnnotations() {
		// Update the JS object
		setProperty(TiC.PROPERTY_ANNOTATIONS, new Object[0]);

		if (TiApplication.isUIThread()) {
			handleRemoveAllAnnotations();
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_REMOVE_ALL_ANNOTATIONS));
		}
	}

	public void handleRemoveAllAnnotations() {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			mapView.removeAllAnnotations();
		}
	}

	public boolean isAnnotationValid(Object annotation) {
		// Incorrect argument types
		if (!(annotation instanceof AnnotationProxy || annotation instanceof String)) {
			Log.e(TAG, "Unsupported argument type for removeAnnotation");
			return false;
		}
		// Marker isn't on the map
		if (annotation instanceof AnnotationProxy
				&& ((AnnotationProxy) annotation).getTiMarker() == null) {
			return false;
		}

		if (annotation instanceof String) {
			TiUIView view = peekView();
			if (view instanceof TiUIMapView) {
				TiUIMapView mapView = (TiUIMapView) view;
				if (mapView.findMarkerByTitle((String) annotation) == null) {
					return false;
				}
			}
		}

		return true;
	}

	private void removeAnnotationByTitle(ArrayList<Object> annoList,
			String annoTitle) {
		for (int i = 0; i < annoList.size(); i++) {
			Object obj = annoList.get(i);
			if (obj instanceof AnnotationProxy) {
				AnnotationProxy annoProxy = (AnnotationProxy) obj;
				String title = TiConvert.toString(annoProxy
						.getProperty(TiC.PROPERTY_TITLE));
				if (title != null && title.equals(annoTitle)) {
					annoList.remove(annoProxy);
					break;
				}
			}
		}
	}

	private void removeAnnoFromList(ArrayList<Object> annoList,
			Object annotation) {
		if (annotation instanceof AnnotationProxy) {
			annoList.remove(annotation);
		} else if (annotation instanceof String) {
			removeAnnotationByTitle(annoList, (String) annotation);
		}
	}

	@Kroll.method
	public void removeAnnotation(Object annotation) {
		if (!(annotation instanceof AnnotationProxy || annotation instanceof String)) {
			Log.e(TAG, "Unsupported argument type for removeAnnotation");
			return;
		}

		// Update the JS object
		Object annotations = getProperty(TiC.PROPERTY_ANNOTATIONS);
		if (annotations instanceof Object[]) {
			ArrayList<Object> annoList = new ArrayList<Object>(
					Arrays.asList((Object[]) annotations));
			removeAnnoFromList(annoList, annotation);
			setProperty(TiC.PROPERTY_ANNOTATIONS, annoList.toArray());
		}

		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			if (TiApplication.isUIThread()) {
				handleRemoveAnnotation(annotation);
			} else {
				TiMessenger.sendBlockingMainMessage(getMainHandler()
						.obtainMessage(MSG_REMOVE_ANNOTATION), annotation);
			}
		}
	}

	@Kroll.method
	public void removeAnnotations(Object annos) {
		// Update the JS object
		Object annotations = getProperty(TiC.PROPERTY_ANNOTATIONS);
		if (annotations instanceof Object[] && annos instanceof Object[]) {
			ArrayList<Object> annoList = new ArrayList<Object>(
					Arrays.asList((Object[]) annotations));
			Object[] annoArray = (Object[]) annos;
			for (int i = 0; i < annoArray.length; i++) {
				Object annotation = annoArray[i];
				removeAnnoFromList(annoList, annotation);
			}
			setProperty(TiC.PROPERTY_ANNOTATIONS, annoList.toArray());
		}

		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			if (TiApplication.isUIThread()) {
				handleRemoveAnnotations((Object[]) annos);
			} else {
				TiMessenger.sendBlockingMainMessage(getMainHandler()
						.obtainMessage(MSG_REMOVE_ANNOTATIONS), annos);
			}
		}
	}

	public void handleRemoveAnnotations(Object[] annotations) {
		for (int i = 0; i < annotations.length; i++) {
			Object annotation = annotations[i];
			if (annotation instanceof AnnotationProxy
					|| annotation instanceof String) {
				handleRemoveAnnotation(annotations[i]);
			}
		}
	}

	public void handleRemoveAnnotation(Object annotation) {
		TiUIMapView mapView = (TiUIMapView) peekView();
		if (mapView.getMap() != null) {
			mapView.removeAnnotation(annotation);
		}
	}

	@Kroll.method
	public void selectAnnotation(Object annotation) {
		if (!isAnnotationValid(annotation)) {
			return;
		}

		if (TiApplication.isUIThread()) {
			handleSelectAnnotation(annotation);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_SELECT_ANNOTATION),
					annotation);
		}
	}

	public void handleSelectAnnotation(Object annotation) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			((TiUIMapView) view).selectAnnotation(annotation);
		}
	}

	@Kroll.method
	public void deselectAnnotation(Object annotation) {
		if (!isAnnotationValid(annotation)) {
			return;
		}

		if (TiApplication.isUIThread()) {
			handleDeselectAnnotation(annotation);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_DESELECT_ANNOTATION),
					annotation);
		}
	}

	public void handleDeselectAnnotation(Object annotation) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			((TiUIMapView) view).deselectAnnotation(annotation);
		}
	}

	@Kroll.method
	public void addRoute(RouteProxy route) {

		if (TiApplication.isUIThread()) {
			handleAddRoute(route);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_ADD_ROUTE), route);

		}
	}

	public void handleAddRoute(Object route) {
		if (route == null) {
			return;
		}
		RouteProxy r = (RouteProxy) route;
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.addRoute(r);

			} else {
				addPreloadRoute(r);
			}
		} else {
			addPreloadRoute(r);
		}

	}

	public void addPreloadRoute(RouteProxy r) {
		if (!preloadRoutes.contains(r)) {
			preloadRoutes.add(r);
		}
	}

	public void removePreloadRoute(RouteProxy r) {
		if (preloadRoutes.contains(r)) {
			preloadRoutes.remove(r);
		}
	}

	public float getMaxZoom() {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			return ((TiUIMapView) view).getMaxZoomLevel();
		} else {
			return 0;
		}
	}

	public float getMinZoom() {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			return ((TiUIMapView) view).getMinZoomLevel();
		} else {
			return 0;
		}
	}

	@Kroll.method
	@Kroll.getProperty
	public float getMaxZoomLevel() {
		if (TiApplication.isUIThread()) {
			return getMaxZoom();
		} else {
			return (Float) TiMessenger.sendBlockingMainMessage(getMainHandler()
					.obtainMessage(MSG_MAX_ZOOM));
		}
	}

	@Kroll.method
	@Kroll.getProperty
	public float getMinZoomLevel() {
		if (TiApplication.isUIThread()) {
			return getMinZoom();
		} else {
			return (Float) TiMessenger.sendBlockingMainMessage(getMainHandler()
					.obtainMessage(MSG_MIN_ZOOM));
		}
	}

	@Kroll.method
	public void removeRoute(RouteProxy route) {
		if (TiApplication.isUIThread()) {
			handleRemoveRoute(route);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_REMOVE_ROUTE), route);

		}
	}

	public void handleRemoveRoute(RouteProxy route) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.removeRoute(route);

			} else {
				removePreloadRoute(route);
			}
		} else {
			removePreloadRoute(route);
		}
	}

	public ArrayList<RouteProxy> getPreloadRoutes() {
		return preloadRoutes;
	}

	/**
	 * Polygons
	 **/
	@Kroll.method
	public void addPolygon(PolygonProxy polygon) {
		if (TiApplication.isUIThread()) {
			handleAddPolygon(polygon);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_ADD_POLYGON), polygon);
		}
	}

	public void handleAddPolygon(Object polygon) {
		if (polygon == null) {
			return;
		}
		PolygonProxy p = (PolygonProxy) polygon;
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.addPolygon(p);
			} else {
				addPreloadPolygon(p);
			}
		} else {
			addPreloadPolygon(p);
		}

	}

	public void addPreloadPolygon(PolygonProxy p) {
		if (!preloadPolygons.contains(p)) {
			preloadPolygons.add(p);
		}
	}

	public void removePreloadPolygon(PolygonProxy p) {
		if (preloadPolygons.contains(p)) {
			preloadPolygons.remove(p);
		}
	}

	@Kroll.method
	public void removePolygon(PolygonProxy polygon) {
		if (TiApplication.isUIThread()) {
			handleRemovePolygon(polygon);
		} else {
			TiMessenger
					.sendBlockingMainMessage(
							getMainHandler().obtainMessage(MSG_REMOVE_POLYGON),
							polygon);

		}
	}

	public void handleRemovePolygon(PolygonProxy polygon) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.removePolygon(polygon);

			} else {
				removePreloadPolygon(polygon);
			}
		} else {
			removePreloadPolygon(polygon);
		}
	}

	public ArrayList<PolygonProxy> getPreloadPolygons() {
		return preloadPolygons;
	}

	public void handleRemoveAllPolygons() {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.removeAllPolygons();
			} else {
				preloadPolygons.clear();
			}
		} else {
			preloadPolygons.clear();
		}
	}


	@Kroll.method
	public void removeAllPolygons() {
		// Update the JS object
		setProperty(MapModule.PROPERTY_POLYGONS, new Object[0]);

		if (TiApplication.isUIThread()) {
			handleRemoveAllPolygons();
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_REMOVE_ALL_POLYGONS));
		}
	}	
	
	/**
	 * EOF Polygons
	 */

	/**
	 * Polylines
	 * 
	 **/
	@Kroll.method
	public void addPolyline(PolylineProxy polyline) {
		if (TiApplication.isUIThread()) {
			handleAddPolyline(polyline);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_ADD_POLYLINE), polyline);
		}
	}

	public void handleAddPolyline(Object polyline) {
		if (polyline == null) {
			return;
		}
		PolylineProxy p = (PolylineProxy) polyline;
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.addPolyline(p);
			} else {
				addPreloadPolyline(p);
			}
		} else {
			addPreloadPolyline(p);
		}

	}

	public void addPreloadPolyline(PolylineProxy p) {
		if (!preloadPolylines.contains(p)) {
			preloadPolylines.add(p);
		}
	}

	public void removePreloadPolyline(PolylineProxy p) {
		if (preloadPolylines.contains(p)) {
			preloadPolylines.remove(p);
		}
	}

	@Kroll.method
	public void removePolyline(PolylineProxy polyline) {
		if (TiApplication.isUIThread()) {
			handleRemovePolyline(polyline);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_REMOVE_POLYLINE),
					polyline);
		}
	}

	public void handleRemovePolyline(PolylineProxy polyline) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.removePolyline(polyline);
			} else {
				removePreloadPolyline(polyline);
			}
		} else {
			removePreloadPolyline(polyline);
		}
	}

	public ArrayList<PolylineProxy> getPreloadPolylines() {
		return preloadPolylines;
	}

	public void handleRemoveAllPolylines() {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.removeAllPolylines();
			} else {
				preloadPolylines.clear();
			}
		} else {
			preloadPolylines.clear();
		}
	}

	@Kroll.method
	public void removeAllPolylines() {
		// Update the JS object
		setProperty(MapModule.PROPERTY_POLYLINES, new Object[0]);

		if (TiApplication.isUIThread()) {
			handleRemoveAllPolylines();
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(
					MSG_REMOVE_ALL_POLYLINES));
		}
	}
	
	
	/**
	 * EOF Polylines
	 */

	@Kroll.method
	public void addCircle(CircleProxy circle) {

		if (TiApplication.isUIThread()) {
			handleAddCircle(circle);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_ADD_CIRCLE), circle);
		}
	}

	public void handleAddCircle(CircleProxy circle) {
		if (circle == null) {
			return;
		}
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.addCircle(circle);
			} else {
				addPreloadCircle(circle);
			}
		} else {
			addPreloadCircle(circle);
		}
	}

	public void addPreloadCircle(CircleProxy c) {
		if (!preloadCircles.contains(c)) {
			preloadCircles.add(c);
		}
	}

	public void removePreloadCircle(CircleProxy c) {
		if (preloadCircles.contains(c)) {
			preloadCircles.remove(c);
		}
	}

	@Kroll.method
	public void removeCircle(CircleProxy circle) {
		if (TiApplication.isUIThread()) {
			handleRemoveCircle(circle);
		} else {
			TiMessenger.sendBlockingMainMessage(
					getMainHandler().obtainMessage(MSG_REMOVE_CIRCLE), circle);

		}
	}

	public void handleRemoveCircle(CircleProxy circle) {
		if (circle == null) {
			return;
		}
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.removeCircle(circle);
			} else {
				removePreloadCircle(circle);
			}
		} else {
			removePreloadCircle(circle);
		}
	}

	@Kroll.method
	public void removeAllCircles() {
		if (TiApplication.isUIThread()) {
			handleRemoveAllCircles();
		} else {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REMOVE_ALL_CIRCLES));
		}
	}

	public void handleRemoveAllCircles() {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView mapView = (TiUIMapView) view;
			if (mapView.getMap() != null) {
				mapView.removeAllCircles();
			} else {
				preloadCircles.clear();
			}
		} else {
			preloadCircles.clear();
		}
	}

	public ArrayList<CircleProxy> getPreloadCircles() {
		return preloadCircles;
	}

	/**
	 * EOF Circles
	 * */

	@Kroll.method
	public void zoom(int delta) {
		if (TiApplication.isUIThread()) {
			handleZoom(delta);
		} else {
			getMainHandler().obtainMessage(MSG_CHANGE_ZOOM, delta, 0)
					.sendToTarget();
		}
	}

	public void handleZoom(int delta) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			((TiUIMapView) view).changeZoomLevel(delta);
		}
	}

	@Kroll.method
	public void setLocation(Object location) {
		if (location instanceof HashMap) {
			HashMap dict = (HashMap) location;
			if (!dict.containsKey(TiC.PROPERTY_LATITUDE)
					|| !dict.containsKey(TiC.PROPERTY_LONGITUDE)) {
				Log.e(TAG,
						"Unable to set location. Missing latitude or longitude.");
				return;
			}
			if (TiApplication.isUIThread()) {
				handleSetLocation(dict);
			} else {
				getMainHandler().obtainMessage(MSG_SET_LOCATION, location)
						.sendToTarget();
			}
		}
	}

	public void handleSetLocation(HashMap<String, Object> location) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			((TiUIMapView) view).updateCamera(location);
		} else {
			Log.e(TAG,
					"Unable set location since the map view has not been created yet. Use setRegion() instead.");
		}
	}
	
	public void refreshAnnotation(AnnotationProxy annotation) {
		TiUIView view = peekView();
		if (view instanceof TiUIMapView) {
			TiUIMapView ourMapView = (TiUIMapView) view;
			final boolean wasSelected = ourMapView.selectedAnnotation == annotation;
			if (TiApplication.isUIThread()) {
				handleAddAnnotation(annotation);
				if (wasSelected) {
					handleSelectAnnotation(annotation);
				}
			} else {
				TiMessenger.sendBlockingMainMessage(getMainHandler()
						.obtainMessage(MSG_ADD_ANNOTATION), annotation);
				if (wasSelected) {
					TiMessenger.sendBlockingMainMessage(getMainHandler()
							.obtainMessage(MSG_SELECT_ANNOTATION), annotation);
				}
			}
		} else {
			Log.e(TAG,
					"Unable to refresh annotation since the map view has not been created yet.");
		}
	}
	
	public String getApiName()
	{
		return "Ti.Map";
	}
}
