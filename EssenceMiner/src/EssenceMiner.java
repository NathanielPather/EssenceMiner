import org.osbot.rs07.api.NPCS;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;

import java.util.*;
import java.awt.Graphics;
import java.awt.Graphics2D;

@ScriptManifest(author = "Lexhanatin", name = "EssenceMiner", info = "Just an empty script :(", version = 0.1, logo = "")

public final class EssenceMiner extends Script  {
	
	NPC portalNpc = null;
	RS2Object portalObj = null;
	Entity portalEntity;
	
	
	Area runeShop = new Area(
		new int[][]{
			{ 3252, 3404 },
	        { 3253, 3404 },
	        { 3255, 3402 },
	        { 3255, 3401 },
	        { 3253, 3399 },
	        { 3252, 3399 }
	        
		}
	);
    
    @Override
    public final int onLoop() throws InterruptedException {
		if (canCollectEssence()) {
			collectEssence();
		}
		else {
			bank();
		}
    return random(150, 200);
    }
    
	private void collectEssence() {
		if (getObjects().closest("Rune Essence") != null) {
			getWalking().webWalk(getPosition());
	    	if (isMining()) {
		        new ConditionalSleep(5000) {
		            @Override
		            public boolean condition() throws InterruptedException {
		                return !isMining();
		            }
		        }.sleep();
		    }
	    	else if (mine()) {
		        new ConditionalSleep(5000) {
		            @Override
		            public boolean condition() throws InterruptedException {
		                return isMining();
		            }
		        }.sleep();
		    }
	    }
		else if (getObjects().closest("Rune Essence") == null){
		    if (!runeShop.contains(myPosition())) {
		        	getWalking().webWalk(runeShop);
		    }
		    else if(runeShop.contains(myPosition())) {
		    	if (teleport()) {
			    	new ConditionalSleep(10000) {
			            @Override
			            public boolean condition() throws InterruptedException {
			                return getObjects().closest("Rune Essence") != null;
			            }
			        }.sleep();
			    }
		    }
		}
	}

	private boolean canCollectEssence() {
	    return !getInventory().contains("Bronze pickaxe") && !getInventory().isFull();
	}
	
	private boolean isMining() {
	    return myPlayer().isAnimating();
	}
	
	private Position getPosition() throws NullPointerException {
		Entity test = getObjects().closest("Rune Essence");
		Position position = new Position(test.getLocalX() + getMap().getBaseX(), test.getLocalY() + getMap().getBaseY(), 0);
		return position;
	}
	
	private boolean mine() {
	    Entity runeEssence = getObjects().closest(objects ->
	            objects.getName().equals("Rune Essence")
	    );
	return runeEssence != null && runeEssence.interact("Mine");
	}
	
	private boolean teleport() {
		return getNpcs().closest("Aubury").interact("Teleport");
	}

    private void bank() throws InterruptedException { 
    	log("Bank method");
    	if (getObjects().closest("Rune Essence") != null) {
    		log("Rune Essence Exists");
    		portalNpc = getNpcs().closestThatContains("Portal");
    		portalObj = getObjects().closestThatContains("Portal");
    		
    		if (portalNpc != null || portalObj != null) {
    			log("Entity not null");
    			if (portalNpc != null) {
    				log("Portal is NPC");
    				portalEntity = portalNpc;
    			}
    			
    			if (portalObj != null) {
    				log("Portal is Object");
    				portalEntity = portalObj;
    			}
    			if (portalEntity != null) {
    				log("Portal Interaction");
    				if (portalEntity.interact("Exit", "Use")) {
    					new ConditionalSleep(8000) {
    			            @Override
    			            public boolean condition() throws InterruptedException {
    			                return !myPlayer().isMoving();
    			            }
    			        }.sleep();
    				}
    			}
    		}
    		else {
    			log ("Entity null");
    		}
    	}
    	else if (!Banks.VARROCK_EAST.contains(myPosition())) {
            getWalking().webWalk(Banks.VARROCK_EAST);
        }
        else if (!getBank().isOpen()) {
            getBank().open();
        }
        else if (!getInventory().isEmpty()) {
            getBank().depositAllExcept("Bronze pickaxe");
        }
        else {
        	getBank().close();
        }
    }
    
    public void onPaint(Graphics g) {
    	g.drawString("Hello, world!", 10, 10);
    }
}