# API文档
## 创建分存图

### 请求参数说明(获取分存图时)

| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | 获取生成分存图-genSplit, 验证-validQR
| secretKey | str | 512 |私钥
| coeK | int | \ | K
| coeN | int | \ | N
| IMEI | str | 30 | IMEI
| IMSI | str | 30 | IMSI
| MAC | str | 60 | MAC
| curType | str | 10 | 货币类型
| walNo | int | \ |钱包编号
<br />


### 示例
```json
{   "reqFlag": "genSplit",
    "secretKey": "1a2aa",
    "coeK":2,
    "coeN":3,
    "IMEI":"23432",
    "IMSI":"21314",
    "MAC":"d3d3",
    "curType":"BTC",
    "walNo":1}
```

<br />

### 返回参数说明(获取分存图时)
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| id| int| \ | 此钱包对应id
| split | ndarray | \ | 分存数组 默认大小为(n, 105, 105, 3) 默认序号为0-4

<br />

### 示例
```json
{"id": 6, "split":...}
```
<br />

### 请求参数说明(验证时)
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| reqFlag| str| \ | 获取生成分存图-genSplit, 验证-validQR
| id | int | \ |请求id, 与获取分存图时返回id对应
| index | list | \ | 验证对应的carrier id 与返回split序号对应
| keys | ndarray | \ | 分存数组 默认大小为(n, 105, 105, 3) 
<br />

### 示例
```json
{"reqFlag":"validQR", "id": 5, "index":[0, 1, 2], "keys": ...}
```

<br />

### 返回参数说明（验证时）（暂定）
| 名称 | 类型 | 最大长度 | 说明 |
| :-----| :----- | :----- | :----- |
| \| int| \ |  \  | 1: success 0: fali -1: 结果二维码无法扫描
