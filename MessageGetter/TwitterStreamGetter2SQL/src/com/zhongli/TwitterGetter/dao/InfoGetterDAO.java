package com.zhongli.TwitterGetter.dao;

import java.util.List;

import com.zhongli.TwitterGetter.model.EarthSqure;
import com.zhongli.TwitterGetter.model.LocArea;
import com.zhongli.TwitterGetter.model.RegInfo;


/**
 * 数据库操作接口类
 * 
 * @author John
 *
 */
public interface InfoGetterDAO {
	
	/**
	 * 重置数据库的状态
	 */
	public void reSetStates();
	
	/**
	 * 从数据库中获取监听区域的信息
	 * 
	 * @param type
	 *            0:新添加未监听的 1:正在监听的 2:被关闭的
	 * @return
	 */
	public List<RegInfo> getRegInfo(int type);

	/**
	 * 改变大区域状态
	 * 
	 * @param id
	 * @param type
	 * @param usetimes
	 */
	public void changeRegState(int id, int type);

	// /**
	// * 获取指定区域所包含的Stream区块以及区块的状态
	// *
	// * @param north
	// * @param south
	// * @param west
	// * @param east
	// * @return
	// */
	// public List<EarthSqure> getSquresByLoc(double north, double south,
	// double west, double east);

	/**
	 * 获取指定区域内的所有用户自定义区块
	 * 
	 * @param north
	 * @param south
	 * @param west
	 * @param east
	 * @return
	 */
	public List<LocArea> getAreaByLoc(double north, double south, double west,
			double east);

	/**
	 * 向数据库中存储Stream区块
	 * 
	 * @param south
	 * @param north
	 * @param west
	 * @param east
	 * @param row
	 * @param col
	 * @param degreepersqure
	 */
	public void saveEarthSqure(double south, double north, double west,
			double east, int row, int col, double degreepersqure);

	/**
	 * 向数据库中存储Stream区块
	 * 
	 * @param es
	 */
	public void saveEarthSqure(EarthSqure es);

	/**
	 * 根据行列查询区块
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public EarthSqure getSqureInfo(int row, int col);

	/**
	 * 从数据库中获取监听区域的信息
	 * 
	 * @param type
	 *            0:新添加未监听的 1:正在监听的 2:被关闭的
	 * @return
	 */
	public List<EarthSqure> getSqureInfo(int type);

	/**
	 * 从数据库中获取使用次数>0但是没有被监视的Stream区块
	 * 
	 * @return
	 */
	public List<EarthSqure> getReadySqure();

	/**
	 * 从数据库中获取使用次数为0但是没有停止监视的块
	 * 
	 * @return
	 */
	public List<EarthSqure> getShouldStopSqure();

	/**
	 * 改变指定区块的状态变量
	 * 
	 * @param id
	 * @param type
	 * @param threadname
	 */
	public void changeSqureState(int id, int type, String threadname);

	/**
	 * 增加区块的使用次数 +1
	 */
	public void squreAddUseTime(int row, int col);

	/**
	 * 减少区块的使用次数 -1
	 */
	public void squreDelUseTime(int row, int col);

	/**
	 * 传入大区域对象，遍历小区域，然后将小区与四个点所在的Stream区块标记出来，
	 * 
	 * @param reg
	 * @return
	 */
	public List<EarthSqure> getStreamSqures(RegInfo reg);

}
