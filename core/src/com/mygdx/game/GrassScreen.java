package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Vector;

public class GrassScreen implements Screen {
    private Preferences prefs = Gdx.app.getPreferences("GrassSimulator3000");
    private FirstPersonCameraController camera_control;
    private PerspectiveCamera camera_perspective;

    private Texture texture_Grass;
    private DecalBatch decalBatch;
    private CameraGrassStrategy grass_cam_strat;

    private Vector<TextureRegion> grass_regions;
    private Vector<Decal> decals;
    private int grass_incremenet = 0;

    private Stage stage;
    private Table table0;
    private Table table1;
    private BitmapFont font;
    private TextButton wind_button;
    private TextButton fps_button;
    private SpriteBatch spriteBatch;

    private int intensity = 0;

    @Override
    public void show() {
        //init
        decals = new Vector<Decal>();
        grass_regions = new Vector<TextureRegion>();
        spriteBatch  = new SpriteBatch();

        //camera
        camera_perspective = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera_perspective.near = 0.1f;
        camera_perspective.far = 100f;
        camera_perspective.position.set(11, 4, 27);
        camera_control = new FirstPersonCameraController(camera_perspective);
        camera_control.setVelocity(4);
        camera_control.setDegreesPerPixel(.15f);

        //wind adjustment
        InputProcessor input_0 = new GestureDetector(new Gesture() {
        }) {
            @Override
            public boolean scrolled(int amount) {
                if (amount == -1) {
                    if (intensity < 15) {
                        grass_cam_strat.adjust_wind(
                                grass_cam_strat.get_x_sway()+0.05f,
                                grass_cam_strat.get_y_sway()+0.01f,
                                grass_cam_strat.get_z_sway()+0.05f);
                        decalBatch.setGroupStrategy(grass_cam_strat);
                        decalBatch.flush();
                        intensity += 1;
                    }
                }
                if (amount == 1) {
                    if (intensity > 0) {
                        grass_cam_strat.adjust_wind(
                                grass_cam_strat.get_x_sway()-0.05f,
                                grass_cam_strat.get_y_sway()-0.01f,
                                grass_cam_strat.get_z_sway()-0.05f);
                        decalBatch.setGroupStrategy(grass_cam_strat);
                        decalBatch.flush();
                        intensity -= 1;
                    }
                }
                return super.scrolled(amount);
            }
        };

        //input control
        InputMultiplexer input_multi = new InputMultiplexer(camera_control);
        input_multi.addProcessor(input_0);
        Gdx.input.setInputProcessor(input_multi);

        //hud
        table0 = new Table();
        table0.setFillParent(true);
        table1 = new Table();
        table1.setFillParent(true);

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), spriteBatch);
        stage.addActor(table0);
        stage.addActor(table1);

        //hud labels
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        font = Assets.manager.get("galax_48.ttf", BitmapFont.class);
        style.font = font;

        wind_button = new TextButton("", style);
        wind_button.pad(15);

        fps_button = new TextButton("", style);
        fps_button.pad(15);

        table0.top().left().padLeft(25).padTop(10);
        table0.add(wind_button);
        table1.top().right().padRight(25).padTop(10);
        table1.add(fps_button);

        //set up grass texture
        texture_Grass = Assets.manager.get(Assets.grass);
        grass_regions.add(new TextureRegion(texture_Grass));

        //batch grass decals
        grass_cam_strat = new CameraGrassStrategy(camera_perspective);
        grass_cam_strat.adjust_wind(0, 0, 0);
        decalBatch = new DecalBatch(grass_cam_strat);
        decalBatch.setGroupStrategy(grass_cam_strat);

        //set patch thickness (space between grass decals)
        float thickness = 0;
        if (prefs.getInteger("grass_type") == 0) {
            thickness = 4.5f;
        } else {
            thickness = MathUtils.random(2f, 3f);
        }

        for(int i = 0; i < 30000; i++){
            decals.add(Decal.newDecal(0.7f, .9f, grass_regions.get(0), true));
        }

        //grid
        int x_ = 100, y_ = 1, z_ = 100;
        int Max = x_*y_*z_;
        for(int p = 0; p < Max; p++){
            int z = p / (x_ * y_);
            int x = (p - z * x_ * y_) % x_;
            //System.out.println("x: " + x + " y:" + y_ + " z:" + z);
            decals.get(grass_incremenet).setPosition(x/thickness, 0, z/thickness);
            decals.get(grass_incremenet).setRotation(MathUtils.random(1) + 60, 0, 0);
            grass_incremenet+=1;
        }
        for(int p = 0; p < Max; p++){
            int z = p / (x_ * y_);
            int x = (p - z * x_ * y_) % x_;
            //System.out.println("x: " + x + " y:" + y_ + " z:" + z);
            decals.get(grass_incremenet).setPosition(x/thickness, 0, z/thickness);
            decals.get(grass_incremenet).setRotation(MathUtils.random(60) + 300, 0, 0);
            grass_incremenet+=1;
        }
        for(int p = 0; p < Max; p++){
            int z = p / (x_ * y_);
            int x = (p - z * x_ * y_) % x_;
            //System.out.println("x: " + x + " y:" + y_ + " z:" + z);
            decals.get(grass_incremenet).setPosition(x/thickness, 0, z/thickness);
            decals.get(grass_incremenet).setRotation(MathUtils.random(360) + 300, 0, 0);
            grass_incremenet+=1;
        }
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
