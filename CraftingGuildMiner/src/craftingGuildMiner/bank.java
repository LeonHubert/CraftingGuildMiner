package craftingGuildMiner;

import java.util.concurrent.Callable;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

public class bank extends Task<ClientContext>{
	
	private final Area bankArea = new Area (new Tile(3009, 3358, 0), new Tile(3017, 3354, 0));
	private int pickAxeID[] = {1265,1267, 1269, 1271, 1273, 1275};
	
	public bank(ClientContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return ctx.inventory.count() > 1 && bankArea.contains(ctx.players.local());
	}

	@Override
	public void execute() {
		
		if(ctx.bank.open())
		{
			ctx.bank.depositAllExcept(pickAxeID);
			ctx.bank.close();
		}
		else
		{
			ctx.bank.open();
			
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.bank.open();
				}
			}, 100, 5);
		}
	}
}