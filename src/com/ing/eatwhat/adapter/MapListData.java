package com.ing.eatwhat.adapter;

public class MapListData {

		String busRoute = null;
		int drawableSource=0;

		public MapListData(String busRoute){
			this.busRoute = busRoute;
		}

		public int getDrawableSource() {
			return drawableSource;
		}
		
		public void setDrawableSource(int drawableSource) {
			this.drawableSource = drawableSource;
		}
		public String getBusRoute() {
			return busRoute;
		}

		public void setBusRoute(String busRoute) {
			this.busRoute = busRoute;
		}
}
