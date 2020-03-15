package craftingGuildMiner;

import java.util.Random;

import org.powerbot.script.rt4.ClientContext;

public class antiBan extends Task<ClientContext>{
	
	public antiBan(ClientContext ctx) {
		super(ctx);
	}

	@Override
	public boolean activate() {
		return ctx.players.local().animation() != -1;
	}
	
	public static int generateRandomInt(int upperRange){
	    Random random = new Random();
	    return random.nextInt(upperRange);
	}

	@Override
	public void execute() 
	{
		if(generateRandomInt(500) == 1)
		{
			switch(generateRandomInt(3))
			{
				case 0:
				{
					ctx.camera.pitch(generateRandomInt(100));
					ctx.camera.angle(generateRandomInt(360));
					break;
				}
				case 1:
				{
					ctx.camera.angle(generateRandomInt(360));
					ctx.camera.pitch(generateRandomInt(100));
					break;
				}
				case 2:
				{
					ctx.input.move(generateRandomInt(700), generateRandomInt(700));
				}
			}
		}
	}
}