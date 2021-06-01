package com.melihakkose.fruitninjaclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

//GIRDILERI YAKALAYABILMEK ICIN "InputProcessor" INTERFACE' ini EKLIYORUZ
public class FruitNinja extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	//ARKAPLAN
	Texture background;

	//OYUNDA KULLANILACAK ASSETLERIN TANIMLANMASI
	Texture apple;
	Texture bill;
	Texture cherry;
	Texture ruby;


	//FONT GENERATOR (BUYUK FONTLARDA PIKSELLESME OLUYOR)
	BitmapFont font;
	FreeTypeFontGenerator fontGenerator;

	//HEALTH , SCORE , FRUIT COUNTER, generate Speed
	int lives=0;
	int score=0;

	private double currentTime;
	private double gameOverTime=-1.0f;

	float genCounter=0;
	private final float startGenSpeed=1.3f;
	float genSpeed=startGenSpeed;

	//RASTGELE SAYI
	Random random = new Random();
	Array<Fruit> fruitArray=new Array<Fruit>();

	@Override
	public void create () {
		batch=new SpriteBatch();
		background = new Texture("ninjabackground.png");

		//ASSET INITIALIZE
		apple=new Texture("apple.png");
		bill=new Texture("bill.png");
		ruby=new Texture("ruby.png");
		cherry=new Texture("cherry.png");

		//RADIUS TANIMLAMA (EKRAN YATAY MI DIKEY MI?)
		Fruit.radius=Math.max(Gdx.graphics.getHeight(),Gdx.graphics.getWidth())/20f;

		//PROCCESSOR ISLEMINI YAPANI YAZMALIYIZ
		Gdx.input.setInputProcessor(this);

		//FONTTYPE GENERATOR
		fontGenerator=new FreeTypeFontGenerator(Gdx.files.internal("slkscre.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params= new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.color= Color.BLACK;
		params.size=70;
		params.characters="0123456789 ScreCutoplay: !.+-";
		font=fontGenerator.generateFont(params);
	}

	@Override
	public void render () {
		batch.begin();
		//BACKGROUND CIZIMI----------
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//FRAME IZLEME BOLUMU---------------

		//ZAMANI ALABILMEK ICIN
		double newTime= TimeUtils.millis()/1000.0;
		System.out.println("newTime: "+newTime);

		//FRAMELERI HESAPLAMAK ICIN
		double frameTime= Math.min(newTime -currentTime,0.3);

		System.out.println("frameTime: "+ frameTime);

		//FRAME FARKI ALABILMEK ICIN
		float deltaTime=(float)frameTime;
		System.out.println("deltaTime: "+deltaTime);
		currentTime=newTime;
		//FRAME IZLEME BOLUMU SONU---------------
        //GAMESTATE KISMI-----------------
		if(lives<=0 && gameOverTime==0f){
			//OYUN BITIMINDE
			gameOverTime=currentTime;
		}
		if(lives>0){
			//OYUN OYNANIRKEN
			genSpeed-=deltaTime*0.015f;
			System.out.println("genSpeed: "+genSpeed);
			System.out.println("genCounter: "+ genCounter);
			if(genCounter<=0f){
				genCounter=genSpeed;
				addItem();
			}else{
				genCounter-=deltaTime;
			}

			//CAN GOSTERGESI
			for(int i=0;i<lives;i++){
				batch.draw(apple,i*40f+50f,Gdx.graphics.getHeight()-50f,40f,40f);
			}

			//MEYVELER ICIN LOOP
			for(Fruit fruit: fruitArray){
				fruit.update(deltaTime);

				//COK ELSE-IF VAR ISE DAHA MANTIKLI
				switch (fruit.type){
					case REGULAR:
						batch.draw(apple,fruit.getPos().x,fruit.getPos().y,Fruit.radius,Fruit.radius);
						break;
					case EXTRA:
						batch.draw(cherry,fruit.getPos().x,fruit.getPos().y,Fruit.radius,Fruit.radius);
						break;
					case ENEMY:
						batch.draw(ruby,fruit.getPos().x,fruit.getPos().y,Fruit.radius,Fruit.radius);
						break;
					case LIFE:
						batch.draw(bill,fruit.getPos().x,fruit.getPos().y,Fruit.radius,Fruit.radius);
						break;
				}

			}

			boolean holdLives=false;

			Array<Fruit> toRemove =new Array<Fruit>();
			for(Fruit fruit: fruitArray){
				if(fruit.outOfScreen()){
					toRemove.add(fruit);

					if(fruit.living && fruit.type==Fruit.Type.REGULAR){
						lives--;
						holdLives=true;
						break;
					}
				}
			}

			if(holdLives){
				for(Fruit f:fruitArray){
					f.living=false;
				}
			}

			for(Fruit f:toRemove){
				fruitArray.removeValue(f,true);
			}


		}
		//GAMESTATE KISMI SONU-----------------

		font.draw(batch,"Score: "+score,90,90);
		if(lives<=0){
			font.draw(batch,"Cut to play!",Gdx.graphics.getWidth()*0.4f,Gdx.graphics.getHeight()*0.6f);

		}

		batch.end();
	}

	//OGELERI EKLEME
	private void addItem(){
		float pos=random.nextFloat()*Math.max(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		Fruit item =new Fruit(new Vector2(pos,-Fruit.radius),new Vector2((Gdx.graphics.getWidth()*0.5f-pos)
				*0.3f+(random.nextFloat()-0.5f),Gdx.graphics.getHeight()*0.5f));

		//RANDOM OGELERI FIRLATIRKEN HANGILERI CIKACAK?
		float type=random.nextFloat();

		if(type>0.98){
			item.type=Fruit.Type.LIFE;
		}else if(type>0.88){
			item.type=Fruit.Type.EXTRA;
		}else if(type>0.78){
			item.type=Fruit.Type.ENEMY;
		}

		fruitArray.add(item);

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		fontGenerator.dispose();

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	//DOKUNMA VE SURUKLEMEYI KULLANACAGIZ
	//KULLANILAN INPUT KISMI-------------
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		if(lives <=0 && currentTime-gameOverTime>2f){
			//MENU MODE
			gameOverTime=0f;
			score=0;
			lives=4;
			genSpeed=startGenSpeed;
			fruitArray.clear();
		}else{
			//GAME MODE

			Array<Fruit> toRemove=new Array<Fruit>();


			int plusScore=0;
			//KULLANICININ TIKLADIGI POZISYON
			Vector2 pos=new Vector2(screenX,Gdx.graphics.getHeight()-screenY);
			for(Fruit f: fruitArray){
				System.out.println("distance: "+pos.dst2((f.pos)));
				System.out.println("distance: "+f.clicked(pos));
				System.out.println("distance: "+Fruit.radius*Fruit.radius+1);

				if(f.clicked(pos)){
					toRemove.add(f);
					switch (f.type){
						case REGULAR:
							plusScore++;
							break;
						case ENEMY:
							lives--;
							break;
						case EXTRA:
							plusScore+=2;
							score++;
							break;
						case LIFE:
							lives++;
							break;
					}
				}
			}
			score+=plusScore*plusScore;

			for(Fruit f: toRemove){
				fruitArray.removeValue(f,true);
			}
		}
		return false;
	}
	//KULLANILAN INPUT KISMI SONU-------------
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
