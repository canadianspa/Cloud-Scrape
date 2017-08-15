package gxs;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

//this should be in files but dont understand how to use file store
public class sharedInformation {
	
	//last po added
	static String firstPoSeen = "4517002484";

	static CopyOnWriteArrayList<StatusReport> currentStatusReports = new CopyOnWriteArrayList<StatusReport>();
	static String logs = "";
	//sku to atrticle codes
	static String skuCsv = "KP-10014,478471,Grand Rapids 29In Deep Inflatable Spa,,Y,N,Y,Turn on \r\n" + 
			"KP-10008,478473,Swift Current 2 29In Deep Foam Wall Spa ,,Y,N,Y,Turn on \r\n" + 
			"KA-10089,432910,Canadian Spa Deluxe Hot Tub Chemical Kit,,Y,N,Y,Turn on \r\n" + 
			"KA-10049,478474,Chlorine Granules 800 GM SS,,Y,N,N,Only Bunnings (Do Not Turn on)\r\n" + 
			"KA-10052,478476,Test Strips 50 SS,,Y,N,N,Only Bunnings (Do Not Turn on)\r\n" + 
			"KA-10010,478478,Portable Microban Filters 4PK SS,,Y,N,Y,Turn on \r\n" + 
			"KA-10039,478479,Spa Aromatherapy Scents 6PK,,Y,N,Y,Turn on \r\n" + 
			"KA-10088,432911,Canadian Spa Portable Chemical Kit,,Y,TRUE,Y,\r\n" + 
			"KC-10021,478231,Brown 78in / 198cm Round Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KS-10003,478232,St. Lawrence Dual Temp 20FT Swim Spa,,,No,Y,Turn on \r\n" + 
			"KF-10024,478234,Square Spa Furniture Corner Bar & Stools,,,No,Y,Turn on \r\n" + 
			"KF-10025,478235,Square Spa Furniture 2-Tier Corner Steps,,,No,Y,Turn on \r\n" + 
			"KF-10026,478237,Square Spa Furniture Corner Planter,,,No,Y,Turn on \r\n" + 
			"KF-10027,478239,Square Spa Furniture Straight Planter,,,No,Y,Turn on \r\n" + 
			"KF-10028,478240,Square Spa Furniture Side Table,,,No,Y,Turn on \r\n" + 
			"KF-10029,478241,Square Spa Furniture Sofa with Cushion,,,No,Y,Turn on \r\n" + 
			"KF-10030,478242,Square Spa Furniture Straight Bar &Stool,,,No,Y,Turn on \r\n" + 
			"KF-10031,478243,Square Spa Furniture Step For 79in spas,,,No,Y,Turn on \r\n" + 
			"KF-10032,478245,Square Spa Furniture Step For 84in spas,,,No,Y,Turn on \r\n" + 
			"KF-10033,478246,Square Spa Furniture Step For 86in spas,,,No,Y,Turn on \r\n" + 
			"KF-10034,478248,Square Spa Furniture Step For 90in spas,,,No,Y,Turn on \r\n" + 
			"KC-10022,478251,Brown 80in / 203cm Round Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10023,478252,Grey 78in / 198cm Round Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10024,478254,Grey 80in / 203cm Round Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10029,478256,Brown 80x75in / 203x190cm Hot tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10033,478257,Brown 82x78in / 208x198cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10041,478259,Brown 88x80in / 223x203cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10047,478260,Brown 94x88in / 238x223cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10030,478262,Grey 80x75in / 203x190cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10034,478264,Grey 82x78in / 208x190cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10042,478265,Grey 88x80in / 223x203cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10048,478266,Grey 94x88in / 238x223cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10001,478267,Brown 78x78in / 198x198cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10007,478269,Brown 90x90in / 228x228cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10008,478270,Brown 92x92in / 233x233cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10009,478272,Brown 94x94in / 238x238cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10010,478273,Brown 96x96in / 243x243cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10015,478275,Grey 78x78in / 198x198cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10017,478277,Grey 90x90in / 228x228cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10018,478278,Grey 92x92in / 233x233cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10019,478280,Grey 94x94in / 238x238cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KC-10020,478281,Grey 96x96in / 243x243cm Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KA-10022,478283,Round Spa Cover Guard 84IN Wide or Less,,,No,Y,Turn on \r\n" + 
			"KA-10020,478285,Spa Cover Guard 84 X 84IN Wide or Less,,,No,Y,Turn on \r\n" + 
			"KA-10021,478286,Spa Cover Guard 96 X 96IN Wide or Less,,,No,Y,Turn on \r\n" + 
			"KA-10012,478288,Spa Aromatherapy Scent Eucalyptus,,,No,Y,Turn on \r\n" + 
			"KA-10013,478291,Spa Aromatherapy Scent Rose,,,No,Y,Turn on \r\n" + 
			"KA-10014,478293,Spa Aromatherapy Scent Green Tea,,,No,Y,Turn on \r\n" + 
			"KA-10015,478294,Spa Aromatherapy Scent Chamomile,,,No,Y,Turn on \r\n" + 
			"KA-10016,478296,Spa Scent Strawberry Champagne,,,No,Y,Turn on \r\n" + 
			"KA-10017,478298,Spa Aromatherapy Scent Lavender,,,No,Y,Turn on \r\n" + 
			"KA-10031,478299,Pre-Filter Screens 2PK,,,No,Y,Turn on \r\n" + 
			"KA-10034,478300,Slip 50 FT2 Filter for Older spas,,,No,Y,Turn on \r\n" + 
			"KA-10004,478301,Slip 50 FT2 Microban Filter,,,No,Y,Turn on \r\n" + 
			"KA-10033,478303,Threaded 50 FT2 Filter for Older spas,,,No,Y,Turn on \r\n" + 
			"KA-10005,478305,Threaded 50 FT2 Microban Filter,,,No,Y,Turn on \r\n" + 
			"KA-10007,478306,Slip 35 FT2 Filter Fits Yukon spa,,,No,Y,Turn on \r\n" + 
			"KA-10007,478308,Slip 35 FT2 Microban Filter,,,No,Y,Turn on \r\n" + 
			"KA-10008,478311,Swim Spa Filter Set ,,,No,Y,Turn on \r\n" + 
			"KA-10009,478313,Swim SpaMicroban Filter Set ,,,No,Y,Turn on \r\n" + 
			"KA-10011,478314,Spa Filter Cleaning Solution,,,No,Y,Turn on \r\n" + 
			"KA-10003,478315,Spa Ball - Absorbs Surface Oils,,,No,Y,Turn on \r\n" + 
			"KA-10018,478316,Headhrest and Cupholder for Grand Rapids,,,No,Y,Turn on \r\n" + 
			"KC-10187,478318,Rio Grande 2 Upgrade Hard Top Cover,,,No,Y,Turn on \r\n" + 
			"KH-10020,478319,Haalifax SF 4 Person Plug & Play Hot Tub,,,No,Y,Turn on \r\n" + 
			"KC-10188,478320,Muskoka 2 Upgrade Hot Tub Cover,,,No,Y,Turn on \r\n" + 
			"KP-10012,478322,29IN Deep Muskoka 2 Foam Wall Spa Coffee,,,No,Y,Turn on \r\n" + 
			"KH-10018,478323,CANADIAN SPA CO. ALBERTA SF 2 X 5HP,,,No,N,Discontinue\r\n" + 
			"KH-10019,478325,Kingston SF 2 X 5HP Hot Tub,,,No,Y,Turn on \r\n" + 
			"KS-10005,432902,16ft Long Endless Current Swim Spa Pool,,,TRUE,,\r\n" + 
			"KS-10004,432903,13ft Long Endless Current Swim Spa Pool,,,TRUE,,\r\n" + 
			"KH-10032,432901,\"Vancouver Dual Pump, 2 Lay Down Seat, Hot Tub Installed\",,,TRUE,,\r\n" + 
			"KH-10025,432899,\"Victoria Dual Pump, All Seating Hot Tub Installed\",,,TRUE,,\r\n" + 
			"KH-10030,432892,Thunder Bay Dual Pump Hot Tub Installed,,,TRUE,,\r\n" + 
			"KH-10022,432897,Quebec Plug & Play (13amp) 3 Person Hot Tub Installed,,,TRUE,,\r\n" + 
			"?,432927,Canadian Spa Fraser Gazebo,,,TRUE,,\r\n" + 
			"KH-10021,432895,Yukon Plug & Play (13amp)  2 Person Hot Tub Installed,,,TRUE,,\r\n" + 
			",432929,Canadian Spa Swift Current,,,FALSE,, DISCON \r\n" + 
			",375259,Rio Grand Portable Spa - VD,,,TRUE,, DISCON \r\n" + 
			",375261,Grand Rapids Portable Spa - VD,,,FALSE,, DISCON \r\n" + 
			"KF-10039,375268,Canadian Spa 5 Piece Rattan Round Spa Furniture Set,,,TRUE,,\r\n" + 
			"?,375269,Canadian Spa Rattan Round Furniture Sofa with Cushion,,,TRUE,,\r\n" + 
			"?,375270,Canadian Spa Rattan Round Furniture Bar with Glass Top,,,TRUE,,\r\n" + 
			"?,375272,Canadian Spa Rattan Round Furniture Curved Step ,,,TRUE,,\r\n" + 
			"?,375271,Canadian Spa Rattan Round Furniture Deep Planter for Round Spa,,,TRUE,,\r\n" + 
			"?,375274,Canadian Spa Rattan Stool for Round Spa,,,TRUE,,\r\n" + 
			",375279,Canadian Spa Furniture Set,,,No,,Duplicate Code do not need this anymore\r\n" + 
			",375280,Canadian Spa Love Seat,,,No,,Duplicate Code do not need this anymore\r\n" + 
			",375282,Canadian Spa Table,,,No,,Duplicate Code do not need this anymore\r\n" + 
			",375284,Canadian Spa Curved Step,,,No,,Duplicate Code do not need this anymore\r\n" + 
			",375283,Canadian Spa Planter,,,No,,Duplicate Code do not need this anymore\r\n" + 
			",375285,Canadian Spa Storage or Stool,,,No,,Duplicate Code do not need this anymore\r\n" + 
			",432904,Canadian Spa Spa Steps,,,TRUE,, DISCON ? Should this be removed from the web? Yes \r\n" + 
			"KF-10019,375262, Curved Black PVC Spa Step ,,,TRUE,,\r\n" + 
			",375275,Canadian Spa Water Filled Seat Booster Cushion,,,FALSE,,Should this be turned on? Yes please turn back on\r\n" + 
			"KA-10032,375267,Canadian Spa Glacier (Fresh Water) Filter Set,,,TRUE,,\r\n" + 
			",375264,4 Filter pack to fit CS portables - VD,,,FALSE,, DISCON \r\n" + 
			"?,375266,Swift Current 2 Upgrade Hot Tub Cover,,,FALSE,,Should this be turned on? Yes please turn back on\r\n" + 
			"KC-10002,432912,Brown 80x80in / 203x203cm Hot Tub Cover ,,,TRUE,,\r\n" + 
			"KC-10012,432914,Grey 80x80in / 203x203cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10003,432915,Brown 82x82in / 208x208cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10013,432917,Grey 82x82in / 208x208cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10004,432919,Brown 84x84in / 213x213cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10014,432921,Grey 84x84in / 213x213cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10005,432922,Brown 86x86in / 218x218cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10015,432923,Grey 86x86in / 218x218cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10006,432924,Brown 88x88in / 223x223cm Hot Tub Cover,,,TRUE,,\r\n" + 
			"KC-10016,432926,Grey 88x88in / 223x223cm Hot Tub Cover ,,,TRUE,,\r\n" + 
			"KA-10026,432907,Bottom Mount Spa Cover Lifter,,,TRUE,,\r\n" + 
			",432909,Canadian Spa Cabinet lifter,,,TRUE,, DISCON \r\n" + 
			"KA-10027,432906,Top Mount Spa Cover Lifter,,,TRUE,,\r\n" + 
			",375276,Canadian Spa SC Cover,,,No,, DISCON";

}
