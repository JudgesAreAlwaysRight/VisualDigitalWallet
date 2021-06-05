# API文档
## 创建分存图
## **url: /vw/generate/**
### 请求参数说明(获取分存图时)

| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | 获取生成分存图-genSplit<br/>其他：页面返回Wrong Request Flag
| secretKey | str | 512 |私钥 仅限256bit字符串
| coeK | int | \ | K
| coeN | int | \ | N
| android_id | str |30 | android_id
| curType | str | 10 | 货币类型
<br />


### 示例
```json
{   "reqFlag": "genSplit",
    "secretKey": "111011010001001001111011010.....",
    "coeK":2,
    "coeN":3,
    "android_id":"23432",
    "curType":"BTC",}
```

<br />



### 返回参数说明(获取分存图时)
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| id| int| \ | 此钱包对应id
| split | ndarray | \ | 分存数组 默认大小为(n, 105, 105) 默认序号为0-4

<br />

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
| keys | ndarray | \ | 分存数组 默认大小为(105, 105) 
<br />

### 示例
```json
{"reqFlag":"cheatDetect", "id": 5, "index":1, "keys": ...}
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
| index | list | \ | 验证对应的carrier id 与返回split序号对应
| keys | ndarray | \ | 分存数组 默认大小为(n, 105, 105) 
<br />

### 示例
```json
{"reqFlag":"validQR", "id": 5, "index":[0, 1, 2], "keys": ...}
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