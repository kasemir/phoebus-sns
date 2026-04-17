
public class TotalKWH
{
	public static void main(String[] args)
	{
		// Data from camonitor -f 2 RTBT_Diag:BCM25I:TotalKWH,
		// updates once per minute:w		
		double total_kWh[] =
		{
				75636718.46,  
				75636746.80,  
				75636775.24,  
				75636803.74,
				75636832.26,
				75636860.80,
				75636889.33, 
				75636917.86, 
				75636946.38, 
				75636974.91, 
				75637003.45, 
				75637031.98  
		};
		int N = total_kWh.length;
		double last = total_kWh[N-1];
		double inc = (last - total_kWh[0]) / N;
		
		System.out.format("Recent TotalKWH            : %.2f\n", last);
		System.out.format("Usual increment            : %.2f\n", inc);
		System.out.format("Smallest possible increment: %e\n", Math.ulp(last));
		
		double limit = last;
		while (Math.ulp(limit) < inc)
			limit *= 10;
		System.out.format("Limit for TotalKWH         : %.2f\n", limit);
		System.out.format("Smallest possible increment: %.2f\n", Math.ulp(limit));		
	}
}
