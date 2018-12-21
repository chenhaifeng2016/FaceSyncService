package com.mycompany.faceedge.facesyncservice.FaceRecognition;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FaceRecognitionService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${FaceRecognition.server}")
    private String faceRecognitionServer;

    @Value("${FaceRecognition.appid}")
    private String appid;

    @Value("${FaceRecognition.groupid}")
    private String groupID;

    public boolean addFace(String orderID, String faceImage) {
        String url = faceRecognitionServer + "/face-api/v3/face/add?appid={1}";

        JSONObject postData = new JSONObject();
        postData.put("group_id", groupID);
        postData.put("user_id", orderID);
        postData.put("image", faceImage);
        postData.put("image_type", "BASE64");
        postData.put("user_info", "");
        postData.put("quality_control", "NONE");
        postData.put("liveness_control", "NONE");


        JSONObject response = restTemplate.postForEntity(url, postData, JSONObject.class, appid).getBody();
        int error_code = response.getIntValue("error_code");

        if (error_code == 0) {
            return true;
        } else {
            String error_msg = response.getString("error_msg");
            System.out.println("添加单程票：" + error_code + ", " + error_msg);
        }

        return false;
    }

    public boolean deleteFace(String orderID) {
        String url = faceRecognitionServer + "/face-api/v3/user/delete?appid={1}";


        JSONObject postData = new JSONObject();
        postData.put("group_id", groupID);
        postData.put("user_id", orderID);

        JSONObject response = restTemplate.postForEntity(url, postData, JSONObject.class, appid).getBody();
        int error_code = response.getIntValue("error_code");

        if (error_code == 0) {
            return true;
        } else {
            String error_msg = response.getString("error_msg");
            System.out.println("删除单程票：" + error_code + ", " + error_msg);
        }

        return false;
    }

}
