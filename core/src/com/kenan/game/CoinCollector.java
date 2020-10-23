package com.kenan.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinCollector extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;//add image to the app or any Visual image
	Texture [] man;
	Texture dizzyFace;
	int playerState=0;
	float  gravity=0.2f;
	float velocity =0;
	int manY=0;
	int pause=0;
	Rectangle manRectangle;
	int gameState=0;

	int score=0;
	BitmapFont font;

	ArrayList<Integer> coinX=new ArrayList<>();
	ArrayList<Integer> coinY=new ArrayList<>();
	ArrayList<Rectangle> coinRectangles =new ArrayList<>();
	Texture coin;
	int coinCount;
	Random random;

	ArrayList<Integer> bombX=new ArrayList<>();
	ArrayList<Integer>bombY=new ArrayList<>();
	ArrayList<Rectangle> bombRectangles =new ArrayList<>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background=new Texture("bg.png");
		man=new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");

		manY=Gdx.graphics.getHeight()/ 2;

		coin=new Texture("coin.png");
		bomb=new Texture("bomb.png");
		dizzyFace=new Texture("dizzy-1.png");

		random=new Random();

		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}
	public void makeCoin(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		coinY.add((int) height);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height=random.nextFloat()*Gdx.graphics.getHeight();
		bombY.add((int) height);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState==1){               //game state ==1
			//Game is On
			//Bombs
			if (bombCount <250){
				bombCount++;
			}else {
				bombCount =0;
				makeBomb();
			}
			//draw the Bombs
			bombRectangles.clear();
			for (int i=0;i<bombX.size();i++){
				batch.draw(bomb,bombX.get(i),bombY.get(i));
				//update the Bomb acts to say we want to move slowly to the left
				bombX.set(i,bombX.get(i)-8);//move faster
				bombRectangles.add(new Rectangle(bombX.get(i),bombY.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//Coins
			if (coinCount <100){
				coinCount++;
			}else {
				coinCount =0;
				makeCoin();
			}
			//draw the Coins
			coinRectangles.clear();//clear before the for
			for (int i=0;i<coinX.size();i++){
				batch.draw(coin,coinX.get(i),coinY.get(i));
				//update the coin acts to say we want to move slowly to the left
				coinX.set(i,coinX.get(i)-4);
				coinRectangles.add(new Rectangle(coinX.get(i),coinY.get(i),coin.getWidth(),coin.getHeight()));
			}
			//Jump when touching the screen
			if (Gdx.input.justTouched()){
				velocity= -10;
			}
			//make the player rendering slower
			if (pause<8){
				pause++;
			}else {
				pause=0;
				if (playerState<3){
					playerState++;

				}else {
					playerState=0;
				}
			}

			velocity+=gravity;
			manY-=velocity;
			if (manY<=0){
				manY=0;
			}

		}else if (gameState==0){
			//Waiting to start
			if (Gdx.input.justTouched()){
				gameState=1;
			}
		}else if (gameState==2){
			                                //Game Over
			if (Gdx.input.justTouched()){
				gameState=1;
				manY=Gdx.graphics.getHeight()/ 2;
				score=0;
				velocity=0;
				coinX.clear();
				coinY.clear();
				coinRectangles.clear();
				coinCount=0;

				bombRectangles.clear();
				bombX.clear();
				bombY.clear();
				bombCount=0;

			}
		}

		if (gameState==2) {
			//Draw the dizzy player
			batch.draw(dizzyFace,Gdx.graphics.getWidth() / 2 - man[playerState].getWidth() / 2, manY);
		}else {
			// normal Mode draw from the start
			batch.draw(man[playerState], Gdx.graphics.getWidth() / 2 - man[playerState].getWidth() / 2, manY);

		}
		manRectangle=new Rectangle(Gdx.graphics.getWidth()/2-man[playerState].getWidth()/2,manY,
				man[playerState].getWidth(),man[playerState].getHeight());

		//check if the player is colliding with the coins
		for (int i=0;i<coinRectangles.size();i++){
			if (Intersector.overlaps(manRectangle,coinRectangles.get(i))){ //check the intersection between the player's rectangle and the coin Rectangle
				score++;
				coinRectangles.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break; //leave after intersection
//				Gdx.app.log("COIN!","COLLISSION!");
			}
		}
		//check if the player is colliding with the Bombs
		for (int i=0;i<bombRectangles.size();i++){
			if (Intersector.overlaps(manRectangle,bombRectangles.get(i))){ //check the intersection between the player's rectangle and the coin Rectangle
				gameState=2;
			}
		}

		font.draw(batch,String.valueOf(score),100,200); //bottom left corner

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
