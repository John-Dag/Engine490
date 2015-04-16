package lightning3d.Network;

import java.util.Comparator;

public class NetStatComparator implements Comparator<NetStatField> {
	@Override
	public int compare(NetStatField f1, NetStatField f2) {
		if (f1.getKills() < f2.getKills())
			return 1;
		else if (f1.getKills() == f2.getKills())
			return 0;
		else
			return -1;
	}
}
