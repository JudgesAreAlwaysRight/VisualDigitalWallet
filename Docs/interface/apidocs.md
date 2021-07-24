# API文档
## 文件上传
## **url: /vw/upload/**
### 请求参数说明

| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | fileUpload|
| id| int| \ | 仅mode为1或2时有用|
|mode|int|\ | 0:generate 1:detect 2:validate|
|type|str| \ | 文件格式|
|file|file| \ | 文件|
<br />


### 示例
```json
{
    "reqFlag": "fileUpload",
    "mode": "0",
    "type": ".wav",
    "file": "...."
}
```

<br />



### 返回参数说明
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| file_name| str| \ |mode = 0 为独一时间戳 <br/> mode =1 为'd_id'<br/> mode = 2 为 'v_id'|

返回为上述即说明上传成功，若失败则为对应错误信息
<br />



## 文件下载
## **url: /vw/download/**
### 请求参数说明

| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | fileDownload|
|id|int| \ |钱包对应id|
|type|str| \ | 文件格式|
<br />


### 示例
```json
{
    "reqFlag": "fileDownload",
    "id": "227",
    "type": ".wav"
}
```

<br />



### 返回参数说明(获取分存图时)
文件命名格式：id_num_new.type 如 227_0_new.wav



<br />

## 创建分存图
## **url: /vw/generate/**
### 请求参数说明(获取分存图时)

| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | 获取生成分存图-genSplit<br/>其他：页面返回Wrong Request Flag
| secretKey | str | 512 |私钥 仅限256bit字符串
| coeK | int | \ | K
| coeN | int | \ | N
| fixed_num | int | \ | 固定图像数量
| needAudio | int | \ | 是否需要音频存储 0-no 1-yes (默认第一个分存作为音频存储)
| audioName | str | \ | 需要的介质音频名
| type | str | \ | 音频类型
| android_id | str |30 | android_id
| curType | str | 10 | 货币类型
<br />


### 示例
```json
{   "reqFlag": "genSplit",
    "secretKey": "1110110100...",
    "coeK":2,
    "coeN":3,
    "fixed_num":1,
    "needAudio":1,
    "audioName":"2021-07-24-18-13-47-306090",
    "type":".wav",
    "android_id" :"hdksjdfhsksdj",
    "curType":"BTC"
}

```

<br />



### 返回参数说明(获取分存图时)
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| id| int| \ | 此钱包对应id
| split | ndarray | \ | 分存数组 默认大小为(n, 105, 105) 默认序号为0-4

<br />

**如果包含音频需要自行去除第一份分存结果**

### 示例
```json
{"id": 6, "split":...}
```
<br />

## 防欺诈

## **url: /vw/detect/**
### 请求参数说明
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | 验证-cheatDetect<br/>其他：页面返回Wrong Request Flag
| id | int | \ |请求id, 与获取分存图时返回id对应
| index | int | \ | 验证对应的carrier id 与返回split序号对应
| isAudio| int | \ | 是否为音频 1-是，此时keys为空 0-否
| type | str| \ | 音频类型
| keys | ndarray | \ | 分存数组 默认大小为(105, 105) 
<br />

### 示例
```json
{
    "reqFlag":"cheatDetect",
    "id":257,
    "index":0,
    "isAudio":1,
    "type": ".wav",
    "keys": ""
}
```

<br />

### 返回参数说明（验证时）
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| id | int |   \ |请求id, 与获取分存图时返回id对应
| flag | int |   \  | 1：验证成功，无篡改 <br/>0：存在篡改 <br/>-1：reqFlag错误
<br />

### 示例
```json
{"id": "106", "flag": 1}
```

## 验证

## **url: /vw/validate/**
### 请求参数说明(验证时)
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | 验证-validQR<br/>其他：页面返回Wrong Request Flag
| id | int | \ |请求id, 与获取分存图时返回id对应
| hasAudio | int | \ | 1 - 有音频 0 - 无音频
| type | str | \ | 音频后缀
| index | list | \ | 验证对应的carrier id 与返回split序号对应
| keys | ndarray | \ | 分存数组 默认大小为(n, 105, 105) 
<br />
### 示例
```json
{
    "reqFlag":"validQR",
    "id":258, 
    "hasAudio": 1,
    "type": ".wav", 
    "index":[1],
    "keys":[[[0, 0..]]]
}

```

<br />

### 返回参数说明（验证时）
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| secretKey | str |   256  | 有结果：对应密钥, 空：验证错误
| flag | int |   \  | 1：验证成功，secretKey返回密钥 <br/>0：合成二维码可扫描但密钥哈希值不符 <br/>-1：无法合成二维码
<br />

### 示例
```json
{"secretKey": "10100...", "flag": 1}
```
<br />

## unfixed图像更新

## **url: /vw/update/**
### 请求参数说明(验证时)
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | 更新-updateQR<br/>其他：页面返回Wrong Request Flag
| id | int | \ |请求id, 与获取分存图时返回id对应
| secretKey | str | 512 |私钥 仅限256bit字符串
| android_id | str |30 | android_id
<br />

### 示例
```json
{   
    "reqFlag": "updateQR",
    "id": 227,
    "secretKey": "111...",
    "android_id" :"hdksjdfhsksdj"
}
```

<br />

### 返回参数说明（验证时）
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| flag | int |   \  | 1：更新成功<br/>0：所有图像均固定，无需更新 <br/>-1：私钥错误
| updated | ndarray |   \  | 有结果：对应更新分存, 空：错误
<br />

### 示例
```json
{"flag": 1, "updated": [[[0, ...]]]}
```