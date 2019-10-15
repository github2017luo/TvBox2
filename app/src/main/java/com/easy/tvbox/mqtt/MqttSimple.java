package com.easy.tvbox.mqtt;

import android.util.Base64;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MqttSimple {

    private MqttAndroidClient mqttAndroidClient;

    public MqttSimple(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }

    public void test() {
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("close", "connectionLost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String body = new String(message.getPayload());
                Log.w("messageArrived", body);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

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
            Log.e("exception", "setPassword", e);
        }

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("connect", "onSuccess");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("connect", "onFailure", exception);
                }
            });
        } catch (Exception e) {
            Log.e("connect", "exception", e);
        }
    }

    public void subscribeToTopic() {
        try {
            final String topicFilter[] = {Config.topic};
            final int[] qos = {1};
            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("subscribe", "success");
//                    publishMessage();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("subscribe", "failed", exception);
                }
            });

        } catch (MqttException ex) {
            Log.e("subscribe", "exception", ex);
        }
    }


    public void publishMessage(String msg) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            mqttAndroidClient.publish(Config.topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("publish", "success:" + msg);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("publish", "failed:" + msg);
                }
            });
        } catch (Exception e) {
            Log.e("publish", "exception", e);
        }
    }

    /**
     * @param text      要签名的文本
     * @param secretKey 阿里云MQ secretKey
     * @return 加密后的字符串
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public String macSignature(String text, String secretKey) throws InvalidKeyException, NoSuchAlgorithmException {
        Charset charset = Charset.forName("UTF-8");
        String algorithm = "HmacSHA1";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secretKey.getBytes(charset), algorithm));
        byte[] bytes = mac.doFinal(text.getBytes(charset));
        // android的base64编码注意换行符情况, 使用NO_WRAP
        String s = new String(Base64.encode(bytes, Base64.NO_WRAP), charset);
        return s;
    }
}
