package com.citydigitalpulse.webservice.dao;

import java.util.ArrayList;
import java.util.List;

import com.citydigitalpulse.webservice.model.collector.LocArea;
import com.citydigitalpulse.webservice.model.collector.RegInfo;

/**
 * 用于控制数据获取程序的数据库
 * 
 * @author zhonglili
 *
 */

public interface CollectorControllerDAO {

	/**
	 * 获得监听的地区名称列表
	 * 
	 * @return
	 */
	public ArrayList<String> getListenPlaces();

	/**
	 * 从数据库中获取监听区域的信息
	 * 
	 * @param type
	 *            0:新添加未监听的 1:正在监听的 2:被关闭的
	 * @return
	 */
	public List<RegInfo> getRegInfo(int type);

	/**
	 * 获取指定地点的所有区域快
	 * 
	 * @return
	 */
	public RegInfo getRegInfoByName(String place_name);

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
	 * 更新地区信息
	 * 
	 * @param reg
	 */
	public void updateRegBoxInfo(RegInfo reg);

}
