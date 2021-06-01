package com.melihakkose.fruitninjaclone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Fruit {
    //EKRANA CIKACAK MEYVELERIN OZELLIKLERINI YAPIYORUZ
    public static float radius=60f;


    //AYRI TIPLER ICIN KULLANACAGIMIZ ICIN ENUM OLUSTURUYORUZ
    public enum Type{
        REGULAR,EXTRA,ENEMY,LIFE

    }
    Type type;


    public boolean living=true;
    //MEYVELERIN HIZI VE POZISYONLARINI CONSTRUCTOR' da BELIRTMEMIZ GEREK
    Vector2 pos,velocity;
    Fruit(Vector2 pos,Vector2 velocity){
        this.pos=pos;
        this.velocity=velocity;
        type=Type.REGULAR;

    }

    //MEYVEYE TIKLANDI MI TIKLANMADI MI KONTROLU
    public boolean clicked(Vector2 click){
        if(pos.dst2(click)<=radius*radius+1){
            return true;
        }
        return false;
    }

    //GUNCEL POZISYON ALMAK ICIN
    public final Vector2 getPos(){
        return pos;
    }

    //BIR MEYVE EKRANIN DISINDA MI DEGIL MI
    public boolean outOfScreen(){
        return (pos.y<-2f*radius);
    }

    //MEYVELERIN GUNCELLENMESINI YAPMALIYIZ HIZINI VE POZISYONUNU BULMAK ICIN
    public void update(float deltaTime){
        velocity.y-=deltaTime*(Gdx.graphics.getHeight()*0.2f);
        velocity.x-=deltaTime*Math.signum(velocity.x)*5f;
        pos.mulAdd(velocity,deltaTime);

        //Signum x>0 1 DONDURUR  x=0 ise 0  x<0 ise -1 DONDURUR
    }
}
