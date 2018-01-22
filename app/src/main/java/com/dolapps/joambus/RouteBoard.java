package com.dolapps.joambus;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteBoard  implements Parcelable{
	byte routeType;
	String routeName;
	String startPointName;
	String midPointName;
	String endPointName;


	// Parcelable을 구현하는 코드, 반드시 아래의 코드들은 추가해주어야 한다.

	// Parcelable 구조체에서 CsutomObject로 읽어오는 함수.

	public RouteBoard(Parcel parcel) {
		this.routeType = parcel.readByte();
		this.routeName = parcel.readString();
		this.startPointName = parcel.readString();
		this.midPointName = parcel.readString();
		this.endPointName = parcel.readString();

	}

	public RouteBoard(byte routeType, String routeName, String startPointName, String midPointName, String endPointName){
		this.routeType = routeType;
		this.routeName = routeName;
		this.startPointName = startPointName;
		this.midPointName = midPointName;
		this.endPointName = endPointName;
	}

	// Parcelable 구조체에 기록하는 함수.

	// 주의 할 점은 반드시 생성자에서 읽어오는 순서와, 기록하는 순서가 같아야 한다.
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeByte(this.routeType);
		dest.writeString(this.routeName);
		dest.writeString(this.startPointName);
		dest.writeString(this.midPointName);
		dest.writeString(this.endPointName);

	}

	// 아래 함수는 구현시 자동으로 생성되는 함수로 별로 신경쓰지 않아도 된다.

	public int describeContents() {

		return 0;

	}

	// Parcelable을 생성하는 코드. 반드시 추가해주어야 한다.

	public static final Parcelable.Creator<RouteBoard> CREATOR = new Parcelable.Creator<RouteBoard>() {

		public RouteBoard createFromParcel(Parcel in) {

			return new RouteBoard(in);

		}

		public RouteBoard[] newArray (int size) {

			return new RouteBoard[size];

		}

	};



}