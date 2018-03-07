package smartcall.android.client;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;

public class AndroidSIPPhoneActivity extends Activity {

	Button recordButton, stopButton, exitButton,playMediaButton;
	SeekBar skbVolume;
	boolean isRecording = false;
	
	//recorder
	static final int frequency = 8000;
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	int recBufSize, playBufSize;
	AudioRecord audioRecord;
	AudioTrack audioTrack;
	MediaRecorder fMediaRecorder;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);

		playBufSize = AudioTrack.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
				channelConfiguration, audioEncoding, playBufSize,
				AudioTrack.MODE_STREAM);

		recordButton = (Button) this.findViewById(R.id.buttonRecord);
		recordButton.setOnClickListener(new ClickStartRecordEvent());

		stopButton = (Button) this.findViewById(R.id.buttonStopRecord);
		stopButton.setOnClickListener(new ClickStopRecordEvent());
		
		playMediaButton =(Button)this.findViewById(R.id.buttonMediaPlay);
		playMediaButton.setOnClickListener(new ClickMediaPlayEvent());
		
		fMediaRecorder = new MediaRecorder();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	class ClickMediaPlayEvent implements View.OnClickListener{
		
		@Override
		public void onClick(View view){
		      if(!myMediaplayer.isPlaying())
                  myMediaplayer.start();  
           }  
		}


	class ClickStartRecordEvent implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			recordButton.setEnabled(false);
			new RecordPlayThread().start();

			/*
			 * fMediaRecorder= new MediaRecorder();
			 * fMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			 * fMediaRecorder
			 * .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			 * fMediaRecorder
			 * .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			 * 
			 * fMediaRecorder.setAudioChannels(1);
			 * fMediaRecorder.setAudioSamplingRate(8000);
			 * 
			 * //fMediaRecorder.setOutputFile("a.wav");
			 * 
			 * try { fMediaRecorder.prepare(); } catch (IllegalStateException e)
			 * { // TODO Auto-generated catch block e.printStackTrace(); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 * 
			 * fMediaRecorder.start();
			 */
		}
	}

	class ClickStopRecordEvent implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			recordButton.setEnabled(true);
			isRecording = false;
		}
	}

	class RecordPlayThread extends Thread {
		public void run() {
			try {
				byte[] buffer = new byte[recBufSize];
				audioRecord.startRecording();// 开始录制
				audioTrack.play();// 开始播放

				while (isRecording) {
					// 从MIC保存数据到缓冲区
					int bufferReadResult = audioRecord.read(buffer, 0,
							recBufSize);

					byte[] tmpBuf = new byte[bufferReadResult];
					System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
					// 写入数据即播放
					audioTrack.write(tmpBuf, 0, tmpBuf.length);
				}
				audioTrack.stop();
				audioRecord.stop();
			} catch (Throwable t) {
				Toast.makeText(AndroidSIPPhoneActivity.this, t.getMessage(),
						1000);
			}
		}
	};
}
