package com.citydigitalpulse.collector.TwitterGetter.model;


/**
 * Stream区块
 * 
 * @author John
 *
 */
public class EarthSqure {
	private int squreID;
	private double south, north, west, east;
	private int row, col;
	private int streamState;
	private int useTimes;
	private double degreePerSqure_lon;
	private String threadName;

	// 地球周长
	private int l_earth = 40075000;
	// 区块大小，单位：m
	private int squre_size = 60000;

	/**
	 * 传入带有参数的构造器
	 * 
	 * @param south
	 * @param north
	 * @param west
	 * @param east
	 * @param row
	 * @param col
	 */
	public EarthSqure(double south, double north, double west, double east,
			int row, int col, double degreePerSqure_lon) {
		this.south = south;
		this.north = north;
		this.west = west;
		this.east = east;
		this.row = row;
		this.col = col;
		this.degreePerSqure_lon = degreePerSqure_lon;
	}

	/**
	 * 只传入行列的构造器
	 * 
	 * @param row
	 * @param col
	 */
	public EarthSqure(int row, int col) {
		this.row = row;
		this.col = col;
		int t_row = Math.abs(row);
		int t_col = Math.abs(col);
		// 北半球的行数
		int row_num = (int) Math.ceil((l_earth / 4) / (double) squre_size);
		// 每行间隔多少�?
		double latCon = 90 / (double) row_num;
		// 地球半径
		double r_earth = l_earth / (2 * Math.PI);
		// 区块东南西北四个边界
		double S, N, W, E;
		S = latCon * (t_row - 1);
		N = latCon * t_row;
		if (N > 180.0) {
			N = 180.0;
		}
		// 对应维度的周�?=cos纬度×地球半径×∏�?2
		// 通过纬度求得周长，除2得到�?个象限的长度
		int l_current = (int) Math.round((Math.cos(Math.toRadians(S)) * r_earth
				* Math.PI * 2)) / 2;
		// �?20公里得到�?共有多少�?
		int col_num = (int) Math.ceil(l_current / (double) squre_size);
		W = 180 * (t_col - 1) / (double) col_num;
		E = 180 * t_col / (double) col_num;
		if (E > 180.0) {
			E = 180.0;
		}
		// 再用180除以列数得到每列间隔多少�?
		this.degreePerSqure_lon = 180 / (double) col_num;
		// 第一象限
		if (row >= 0 && col >= 0) {
			this.south = S;
			this.north = N;
			this.west = W;
			this.east = E;
		} else
		// 第二象限
		if (row > 0 && col < 0) {
			this.south = S;
			this.north = N;
			this.west = -E;
			this.east = -W;
		} else
		// 第三象限
		if (row < 0 && col < 0) {
			this.south = -N;
			this.north = -S;
			this.west = -E;
			this.east = -W;
		} else
		// 第四象限
		{
			this.south = -N;
			this.north = -S;
			this.west = W;
			this.east = E;
		}
	}

	/**
	 * 传入经纬度得到所在的区块
	 * 
	 * @param lat
	 * @param lon
	 */
	public EarthSqure(double lat, double lon) {
		// �?查输入是否符合规�?
		double t_lat = Math.abs(lat);
		double t_lon = Math.abs(lon);
		if (t_lat >= 0 && t_lat <= 90 && t_lon <= 180 && t_lon >= 0) {

			// 该点�?在区块行号和列号
			int row, col;
			// 北半球的行数
			int row_num = (int) Math.ceil((l_earth / 4) / (double) squre_size);
			// 地球半径
			double r_earth = l_earth / (2 * Math.PI);
			// 计算区块间的纬度�?
			double latCon = 90 / (double) row_num;
			row = (int) (t_lat / latCon);
			// 对应维度的周�?=cos纬度×地球半径×∏�?2
			// 通过纬度求得周长，除2得到�?个象限的长度
			int l_current = (int) Math.round((Math.cos(Math.toRadians((row)
					* latCon))
					* r_earth * Math.PI * 2) / 2.0);
			// �?20公里得到�?共有多少�?
			int col_num = (int) Math.ceil(l_current / (double) squre_size);
			// 再用180除以列数得到每列间隔多少�?
			double degPerSqure_lon = 180 / (double) col_num;
			// 再用输入的经度除以这个差值向下取整得到所在区块的列数
			col = (int) (t_lon / degPerSqure_lon);
			double S = latCon * row;
			double N = latCon * (row + 1);
			if (N > 180.0) {
				N = 180.0;
			}

			double W = 180 * col / (double) col_num;
			double E = 180 * (col + 1) / (double) col_num;
			if (E > 180.0) {
				E = 180.0;
			}
			this.degreePerSqure_lon = degPerSqure_lon;
			// 第一象限
			if (lat >= 0 && lon >= 0) {
				this.south = S;
				this.north = N;
				this.west = W;
				this.east = E;
				this.row = row + 1;
				this.col = col + 1;
			} else
			// 第二象限
			if (lat > 0 && lon < 0) {
				this.south = S;
				this.north = N;
				this.west = -E;
				this.east = -W;
				this.row = row + 1;
				this.col = -(col + 1);
			} else
			// 第三象限
			if (lat < 0 && lon < 0) {
				this.south = -N;
				this.north = -S;
				this.west = -E;
				this.east = -W;
				this.row = -(row + 1);
				this.col = -(col + 1);
			} else
			// 第四象限
			{
				this.south = -N;
				this.north = -S;
				this.west = W;
				this.east = E;
				this.row = -(row + 1);
				this.col = col + 1;
			}
		} else {
			System.out.println("Input Error...");
		}

	}

	// �?测一个点是否在这个区域内
	public boolean containThis(double lat, double lon) {
		if (lat >= this.south && lat <= this.north && lon >= this.west
				&& lon <= this.east) {
			return true;
		}
		return false;
	}

	public int getSqureID() {
		return squreID;
	}

	public void setSqureID(int squreID) {
		this.squreID = squreID;
	}

	public int getStreamState() {
		return streamState;
	}

	public void setStreamState(int streamState) {
		this.streamState = streamState;
	}

	public int getUseTimes() {
		return useTimes;
	}

	public void setUseTimes(int useTimes) {
		this.useTimes = useTimes;
	}

	public double getSouth() {
		return south;
	}

	public double getNorth() {
		return north;
	}

	public double getWest() {
		return west;
	}

	public double getEast() {
		return east;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public double getDegreePerSqure_lon() {
		return degreePerSqure_lon;
	}

	@Override
	public String toString() {
		return "EarthSqure [squreID=" + squreID + ", south=" + south
				+ ", north=" + north + ", west=" + west + ", east=" + east
				+ ", row=" + row + ", col=" + col + ", streamState="
				+ streamState + ", useTimes=" + useTimes
				+ ", degreePerSqure_lon=" + degreePerSqure_lon + "]";
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

}
