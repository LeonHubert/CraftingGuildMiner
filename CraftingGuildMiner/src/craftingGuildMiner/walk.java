package craftingGuildMiner;

import java.util.concurrent.Callable;

import org.powerbot.script.Area;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.TilePath;

public class walk extends Task<ClientContext>{
	
	static Tile doorTileInside = new Tile(2933, 3288, 0);
	static Tile doorTileOutside = new Tile(2933, 3289, 0);
	static Tile miningTile = new Tile(2941, 3282, 0);
	private int doorID = 14910;
	private final Area bankArea = new Area (new Tile(3009, 3358, 0), new Tile(3017, 3354, 0));
	private final Area doorAreaInside = new Area (new Tile(2932, 3289, 0), new Tile(2934, 3287, 0));
	private final Area doorAreaOutside = new Area (new Tile(2932, 3290, 0), new Tile(2934, 3288, 0));
	private final Area mineArea = new Area (new Tile(2936, 3292, 0), new Tile(2944, 3275, 0));
	
	public final static Tile[] pathToBank = {new Tile(2938, 3302, 0), new Tile(2952, 3306, 0), new Tile(2966, 3306, 0), new Tile(2979, 3307, 0), new Tile(2993, 3311, 0), new Tile(3003, 3319, 0), new Tile(3007, 3334, 0), new Tile(3007, 3347, 0), new Tile(3012, 3355, 0)};
	public final static Tile[] pathToGuild = {new Tile(3007, 3346, 0), new Tile(3006, 3333, 0), new Tile(3001, 3319, 0), new Tile(2991, 3310, 0), new Tile(2979, 3307, 0), new Tile(2967, 3300, 0), new Tile(2952, 3297, 0), new Tile(2941, 3296, 0), new Tile(2933, 3289, 0)};
	
	public walk(ClientContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return (ctx.inventory.isFull() && !bankArea.contains(ctx.players.local())) || (!ctx.inventory.isFull() && !mineArea.contains(ctx.players.local()));
	}
	
	private void walkPath(TilePath tilePath, Area targetArea) {
        while(!targetArea.contains(ctx.players.local())) {
            final Tile nextTile = tilePath.next();
            ctx.movement.step(nextTile);
            Condition.wait(new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    return !ctx.players.local().inMotion();
                }
            });
        }
    }

	@Override
	public void execute() {
		
		if(ctx.movement.energyLevel() >= 80 && !ctx.movement.running())
		{
			ctx.movement.running(true);
		}
		
		if(ctx.inventory.isFull())
		{
			if(mineArea.contains(ctx.players.local()))
			{
				ctx.movement.step(doorTileInside);
				
				Condition.wait(new Callable<Boolean>() {
		            @Override
		            public Boolean call() throws Exception {
		                return !ctx.players.local().inMotion();
		            }
		        }, 500, 10);
			}
			else if(doorAreaInside.contains(ctx.players.local()))
			{
				GameObject door = ctx.objects.select().id(doorID).nearest().poll();
				
				ctx.camera.turnTo(door);
				
				door.interact("Open");
				
				Condition.wait(new Callable<Boolean>() {
		            @Override
		            public Boolean call() throws Exception {
		                return ctx.players.local().tile() == doorTileOutside;
		            }
		        }, 100, 10);
			}
			else if(doorAreaOutside.contains(ctx.players.local()))
			{
				walkPath(ctx.movement.newTilePath(pathToBank), bankArea);
			}
		}
		else
		{
			if(bankArea.contains(ctx.players.local()))
			{
				walkPath(ctx.movement.newTilePath(pathToGuild), doorAreaOutside);
			}
			else if(doorAreaOutside.contains(ctx.players.local()))
			{
				GameObject door = ctx.objects.select().id(doorID).nearest().poll();
				
				ctx.camera.turnTo(door);
					
				door.interact("Open");
					
				Condition.wait(new Callable<Boolean>() {
			           @Override
			           public Boolean call() throws Exception {
			               return ctx.players.local().inMotion();
			           }
			       }, 200, 10);
				
				ctx.movement.step(miningTile);
				
				Condition.wait(new Callable<Boolean>() {
		            @Override
		            public Boolean call() throws Exception {
		                return !ctx.players.local().inMotion();
		            }
		        }, 500, 10);
			}
		}
	}
}