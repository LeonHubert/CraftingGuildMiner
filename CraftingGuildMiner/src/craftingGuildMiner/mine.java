package craftingGuildMiner;

import java.util.concurrent.Callable;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;

public class mine extends Task<ClientContext>{
	
	private int goldID[] = {11370, 11371};
	private int clayID[] = {11362, 11363};
	private int silverID[] = {11368, 11369};
	static int targetOreIndex = -1;
	private final Area mineArea = new Area (new Tile(2936, 3292, 0), new Tile(2944, 3275, 0));
	
	public mine(ClientContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return ctx.players.local().animation() == -1 && !ctx.inventory.isFull() && mineArea.contains(ctx.players.local());
	}

	@Override
	public void execute() {
		
		GameObject rock = null;
		
		switch(targetOreIndex)
		{
			case 0:
			{
				rock = ctx.objects.select().id(goldID).nearest().poll();
				break;
			}
			case 1:
			{
				rock = ctx.objects.select().id(silverID).nearest().poll();
				break;
			}
			case 2:
			{
				rock = ctx.objects.select().id(clayID).nearest().poll();
				break;
			}
				
		}
		
		if(rock.valid())
		{
			if(rock.inViewport())
			{
				rock.click();
				
				Condition.wait(new Callable<Boolean>() {
		            @Override
		            public Boolean call() throws Exception {
		                return ctx.players.local().animation() != -1;
		            }
		        }, 250, 10);
			}
			else ctx.camera.turnTo(rock);
		}
	}
}