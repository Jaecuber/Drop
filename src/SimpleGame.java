import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SimpleGame implements ApplicationListener {
    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Texture fireballTexture;
    Sound dropSound;
    Music music;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Sprite bucketSprite;
    Vector2 touchPos;
    ArrayList<Sprite> dropSprites;
    ArrayList<Sprite> fireBalls;
    float dropTimer;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    Rectangle fireRectangle;

    @Override
    public void create() {
        backgroundTexture = new Texture("assets/background.png");
        bucketTexture = new Texture("assets/bucket.png");
        dropTexture = new Texture("assets/drop.png");
        fireballTexture = new Texture("assets/fireball.png");
        
        dropSound = Gdx.audio.newSound(Gdx.files.internal("assets/drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("assets/music.mp3"));
        
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);
        
        touchPos = new Vector2();
        
        dropSprites = new ArrayList<>();
        fireBalls = new ArrayList<>();
        
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    public void fire(){
        float fireWidth = 1;
        float fireHeight = 1;

        Sprite fireSprite = new Sprite(fireballTexture);
        fireSprite.setSize(fireWidth, fireHeight);
        fireSprite.setX(bucketSprite.getX());
        fireSprite.setY(bucketSprite.getY());
        fireSprite.setRotation(1);
        fireBalls.add(fireSprite);

        fireRectangle = new Rectangle();
        
    }

    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            bucketSprite.translateX(speed * delta);
        }  
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            bucketSprite.translateX(-speed * delta);
        } 
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)){
            bucketSprite.translateY(speed * delta);
        } 
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)){
            bucketSprite.translateY(-speed * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            fire();
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));

        float delta = Gdx.graphics.getDeltaTime();
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        for (int i = dropSprites.size() - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight) dropSprites.remove(i);
            else if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.remove(i);
                dropSound.play();
            }
        }
        for(int i = fireBalls.size() - 1; i >= 0; i--){
            Sprite fireSprite = fireBalls.get(i);
            float dropWidth = fireSprite.getWidth();
            float dropHeight = fireSprite.getHeight();

            fireSprite.translateY(2.5f * delta);
            dropRectangle.set(fireSprite.getX(), fireSprite.getY(), dropWidth, dropHeight); 
        }

        dropTimer += delta;
        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        bucketSprite.draw(spriteBatch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }
        for(Sprite fire : fireBalls){
            fire.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void dispose() {
        
    }
}
