package com.easy.tvbox.mqtt;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.easy.tvbox.event.MtMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MqttSimple {

    private MqttAndroidClient mqttAndroidClient;

    public MqttSimple(Context applicationContext) {
        mqttAndroidClient = new MqttAndroidClient(applicationContext, Config.serverUri, Config.clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d("Mqtt", "connectionLost==>" + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String msg = new String(message.getPayload());
                Log.d("Mqtt", "收到消息==》" + msg);
                try {
                    MtMessage mtMessage = JSON.parseObject(msg, MtMessage.class);
                    Log.d("Mqtt", "收到消息==》" + mtMessage.toString());
                    EventBus.getDefault().post(mtMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("Mqtt", "deliveryComplete==>" + token.toString());
            }
        });
    }

    public MqttConnectOptions getConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setConnectionTimeout(10000);
        mqttConnectOptions.setKeepAliveInterval(90);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        try {
            // 参考 https://help.aliyun.com/document_detail/54225.html
            // Signature 方式
            mqttConnectOptions.setUserName("Signature|" + Config.accessKey + "|" + Config.instanceId);
            mqttConnectOptions.setPassword(macSignature(Config.clientId, Config.secretKey).toCharArray());

            /**
             * Token方式
             *  mqttConnectOptions.setUserName("Token|" + Config.accessKey + "|" + Config.instanceId);
             *  mqttConnectOptions.setPassword("RW|xxx");
             */
        } catch (Exception e) {
            Log.e("Mqtt", "setPassword Exception=>", e);
        }
        return mqttConnectOptions;
    }

    public void connect(String shopId) {
        try {
            mqttAndroidClient.connect(getConnectOptions(), null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt", "onSuccess");
                    subscribeToTopic(shopId);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Mqtt", "onFailure==>" + exception);
                }
            });
        } catch (Exception e) {
            Log.e("Mqtt", "exception11==>" + e);
        }
    }

    private void subscribeToTopic(String shopId) {
        try {
            String[] topicFilter = {Config.topic +"/BOX/"+ shopId};
            final int[] qos = {1};
            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt", "Topic==>success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Mqtt", "Topic==>failed", exception);
                }
            });
        } catch (Exception ex) {
            Log.e("Mqtt", "Topic==>exception", ex);
        }
    }


    public void publishMessage(String msg) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            mqttAndroidClient.publish(Config.topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt", "publish=>success:" + msg);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "publish=>failed:" + msg);
                }
            });
        } catch (Exception e) {
            Log.e("Mqtt", "publish=>exception", e);
        }
    }

    /**
     * @param text      要签名的文本
     * @param secretKey 阿里云MQ secretKey
     * @return 加密后的字符串
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    private String macSignature(String text, String secretKey) throws InvalidKeyException, NoSuchAlgorithmException {
        Charset charset = Charset.forName("UTF-8");
        String algorithm = "HmacSHA1";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secretKey.getBytes(charset), algorithm));
        byte[] bytes = mac.doFinal(text.getBytes(charset));
        // android的base64编码注意换行符情况, 使用NO_WRAP
        String s = new String(Base64.encode(bytes, Base64.NO_WRAP), charset);
        return s;
    }

    public void onDestroy() {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.unregisterResources();
        }
    }
}
