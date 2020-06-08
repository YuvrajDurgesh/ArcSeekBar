# ArcSeekBar

ArcSeekBar is a android Library moreover a modified version of <a href="https://github.com/GoodieBag/ProtractorView">ProtractorView</a>

<img src="https://github.com/YuvrajDurgesh/ArcSeekBar/blob/master/IMG_20200608_194614%5B1%5D.JPG" width="200" height="450">

# Add to your project

Add remote marvel URL

    allprojects {
        repositories {

          maven { url 'https://jitpack.io' }
        }
      }

Library dependency

    dependencies {
              implementation 'com.github.YuvrajDurgesh:ArcSeekBar:1.0.0'
      }
      
  
# Features 
  Semi circular Seekbar
  Custom minimum and maximum range value
  Enable/disable option
  Customization of all it's value through XML or you can do by programatically
  
  # Uses
  
  ## XML
  
      <com.yuvrajdurgesh.arcseekbar.ArcSeekBar
        android:id="@+id/arcSeekbar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:angleTextSize="10sp"
        app:arcColor="#4a4a4a"
        app:arcWidth="4dp"
        app:arcProgressColor="@color/md_blue_500"
        app:progressWidth="4dp"
        app:roundEdges="true"
        app:textColor="#000"
        app:textProgressColor="@color/md_blue_500"
        app:bottomText="Temperature"
        app:bottomTextColor="@color/md_blue_500"
        app:centralTextColor="@color/md_grey_500"
        app:tickColor="#4a4a4a"
        app:tickIntervals="15"
        app:tickLength="10dp"
        app:tickOffset="18dp"
        app:tickProgressColor="@color/md_blue_500"
        app:ticksBetweenLabel="two"
        app:touchInside="false"
        app:setValue="45"
        app:minValue="-10"
        app:maxValue="60"
        app:ordinalIndicator="°"
        <!-- if you want to show central text as a progress value use this -->
        app:centerTextAsValue="true"
        app:centralTextSize="20sp"
        app:bottomTextSize="18sp"/>
        
  
 ## Java
    
    private ArcSeekBar arcSeekBar;
    
    arcSeekBar = findViewById(R.id.arcSeekbar);
    
    arcSeekBar.setValue(25);
    double value = arcSeekBar.getValue();
    arcSeekBar.setOrdinalIndicator("°");
    arcSeekBar.setBottomText("Temperature");
    //if you want to set custom central text instead of showing value use this method
    arcSeekBar.setCentralText();
    ...
    
    
### Listener

        arcSeekBar.setOnArcSeekBarChangeListener(new ArcSeekBar.OnArcSeekBarChangeListener() {
            @Override
            public void onProgressChanged(ArcSeekBar ArcSeekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(ArcSeekBar ArcSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(ArcSeekBar ArcSeekBar) {

            }
        });
        
        
### Thanks
Thanks to <a href="https://github.com/GoodieBag/ProtractorView">ProtractorView</a><br>
This library is moreover a modified version of <a href="https://github.com/GoodieBag/ProtractorView">ProtractorView</a> Library
