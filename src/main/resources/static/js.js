function saveUser() {
    let selfId = document.getElementById("selfId").value;
    let key = document.getElementById("key").value;
    axios.get("/saveUser", {
        params: {
            userId: selfId,
            key: key,
        }
    }).then(res => {
        // 接口数据
        console.log(res.data)
    });
}

function processErrorResult(result) {
    if (result == null) {
        alert("返回值为空");
        return false;
    }

    let code = result.code;
    let message = result.message;
    if (code === 0) {
        return true;
    }

    if (code === -1) {
        alert(message);
    } else {
        alert("系统错误！\n" + message);
    }
    return false;
}

function resetPage() {
    let a = document.getElementById("page");
    a.value = 1;
}

function nextPage() {
    let page = document.getElementById("page").value;
    if (page != null) {
        document.getElementById("page").value = parseInt(page) + 1;
    }
    getAuctionItems();
}

function frontPage() {
    let page = document.getElementById("page").value;
    if (page != null) {
        document.getElementById("page").value = parseInt(page) - 1;
    }
    getAuctionItems();
}

function saveTemplate() {
    let templateName = document.getElementById("template-name").value;
    let templateId = document.getElementById("template-id").value;
    if (templateId === "" || templateName === "") {
        alert("有参数为空");
    }

    let url = "/saveTemplate";
    axios.get(url, {
        params: {
            templateName: templateName,
            templateId: templateId
        }
    }).then(res => {
        // 接口数据
        let data = res.data;
        console.log(data);
    });
}

function getAuctionItems() {
    document.getElementById("result-body").innerHTML = "";
    let order = 2;
    let sort = true;
    let page = document.getElementById("page").value;
    let itemName = document.getElementById("itemName").value;
    let selfId = document.getElementById("selfId").value;
    let userId = document.getElementById("userId").value;

    let orderTime = document.getElementById("orderTime");
    let orderName = document.getElementById("orderName");
    let orderPrice = document.getElementById("orderPrice");
    let orderSeller = document.getElementById("orderSeller");
    if (orderTime.checked) {
        order = 2;
    } else if (orderName.checked) {
        order = 0;
    } else if (orderPrice.checked) {
        order = 4;
    } else if (orderSeller.checked) {
        order = 3;
    }

    let asc = document.getElementById("asc");
    let desc = document.getElementById("desc");
    if (asc.checked) {
        sort = false;
    } else if (desc.checked) {
        sort = true;
    }

    if (page == null) {
        page = 1;
    }

    if (itemName == null) {
        itemName = "";
    }

    let url = "/getAuctionItems";

    axios.get(url, {
        params: {
            page: page,
            order: order,
            sort: sort,
            itemName: itemName,
            userId: userId,
            selfId: selfId
        }
    }).then(res => {
        // 接口数据
        let data = res.data;
        let success = processErrorResult(data);
        if (!success) {
            return;
        }

        console.log(data);
        data = data.result;
        if (data == null) {
            return;
        }

        let result = new Result(data.total, data.value, data.message, data.items);
        console.log(result);
        if (result == null) {
            return;
        }

        document.getElementById("total-count").value = result.total;
        document.getElementById("total-page").value = Math.ceil(result.total / 20);

        buildTable(result.items);
    });

    document.getElementById("template-name").value = itemName;
    document.getElementById("template-id").value = "";
}

function getAuctionPriceOder(priceType, sort) {
    let itemName = document.getElementById("itemName-price").value;
    let selfId = document.getElementById("selfId").value;

    if (itemName === "") {
        alert("物品名称必填");
    }

    let url = "/getAuctionPriceOder";

    axios.get(url, {
        params: {
            sort: sort,
            itemName: itemName,
            selfId: selfId,
            priceType: priceType,
        }
    }).then(res => {
        // 接口数据
        let data = res.data;
        let success = processErrorResult(data);
        if (!success) {
            return;
        }

        data = data.result;
        console.log(data);
        if (data == null) {
            return;
        }

        let result = new Result(data.total, data.value, data.message, data.items);
        console.log(result);
        if (result == null) {
            return;
        }

        document.getElementById("total-count").value = result.total;
        document.getElementById("total-page").value = Math.ceil(result.total / 20);

        buildTable(result.items);
    });

    document.getElementById("template-name").value = "";
    document.getElementById("template-id").value = "";
}


function buildItem(item) {
    if (item == null) {
        return null;
    }

    return new Item(item.auctionID, item.auctioneerID, item.auctioneerName, item.beginTimeString, item.buyerID, item.count, item.itemID, item.mouthful, item.payType, item.price, item.rise, item.pic, item.templateId, item.templateName, item.unitPrice, item.unitMouthfulPrice, item.userID, item.validDate, item.buyerName, item.userDefinePrice);
}

function Result(total, value, message, items) {
    this.total = total;
    this.value = value;
    this.message = message;

    let itemList = [];
    if (items != null && items.length !== 0) {
        for (let i = 0; i < items.length; i++) {
            let item = items[i];
            itemList.push(buildItem(item));
        }
    }
    this.items = itemList;
}

function Item(auctionID, auctioneerID, auctioneerName, beginTimeString, buyerID, count, itemID, mouthful, payType, price, rise, pic, templateId, templateName, unitPrice, unitMouthfulPrice, userID, validDate, buyerName, userDefinePrice) {
    this.auctionID = auctionID;
    this.auctioneerID = auctioneerID;
    this.auctioneerName = auctioneerName;
    this.beginTimeString = beginTimeString;
    this.buyerID = buyerID;
    this.count = count;
    this.itemID = itemID;
    this.mouthful = mouthful;
    this.payType = payType;
    this.price = price;
    this.rise = rise;
    this.pic = pic;
    this.templateId = templateId;
    this.templateName = templateName == null ? "未知" : templateName;
    this.unitPrice = unitPrice;
    this.unitMouthfulPrice = unitMouthfulPrice;
    this.userID = userID;
    this.validDate = validDate;
    this.buyerName = buyerName == null ? "" : buyerName;
    this.userDefinePrice = userDefinePrice;
}


function buildTable(items) {
    let tableHtml = '' +
        '    <tr>\n' +
        '        <th>序号</th>\n' +
        '        <th class="table-item-name has-not-buyer">物品名称</th>\n' +
        '        <th>数量</th>\n' +
        '        <th>竞拍价</th>\n' +
        '        <th>一口价</th>\n' +
        '        <th class="bid-unit-black">竞拍价单价</th>\n' +
        '        <th class="mouth-unit-black">一口价单价</th>\n' +
        '        <th class="expected-price">期望价格</th>\n' +
        '        <th class="table-seller">出售者</th>\n' +
        '        <th class="table-time">时间</th>\n' +
        '        <th>有效期</th>\n' +
        '        <th>templateId</th>\n' +
        '        <th>pic</th>\n' +
        '        <th>竞拍</th>\n' +
        '    </tr>';

    let resultBody = document.getElementById("result-body");
    resultBody.innerHTML = "";
    if (items == null || items.length === 0) {
        return;
    }

    let innerHtml = "";
    let tid = items[0].templateId;
    let tidSame = true;
    for (let i = 0; i < items.length; i++) {
        let item = items[i];
        item.unitPrice = parseFloat(item.unitPrice.toFixed(2));
        item.unitMouthfulPrice = parseFloat(item.unitMouthfulPrice.toFixed(2));
        let htmlTemp = tableHtml
            .replace("序号", i)
            .replace("物品名称", item.templateName)
            .replace("数量", item.count)
            .replace("竞拍价", item.price)
            .replace("一口价", item.mouthful)
            .replace("竞拍价单价", item.unitPrice)
            .replace("一口价单价", item.unitMouthfulPrice)
            .replace("期望价格", item.userDefinePrice == null ? "" : item.userDefinePrice)
            .replace("出售者", item.auctioneerName)
            .replace("时间", item.beginTimeString)
            .replace("有效期", item.validDate)
            .replace("templateId", item.templateId)
            .replace("pic", item.pic)
            .replace("竞拍", item.buyerName)
        ;
        if (item.userDefinePrice != null) {
            if (item.unitPrice > 0 && item.userDefinePrice > item.unitPrice) {
                console.log(item.userDefinePrice);
                console.log(item.unitPrice);
                console.log(item.userDefinePrice > item.unitPrice);
                console.log()
                htmlTemp = htmlTemp.replace("bid-unit-black", "bid-unit-red");
            }

            if (item.unitMouthfulPrice > 0 && item.userDefinePrice > item.unitMouthfulPrice) {
                htmlTemp = htmlTemp.replace("mouth-unit-black", "mouth-unit-red");
            }


        }

        if (item.buyerName != null && item.buyerName !== "") {
            htmlTemp = htmlTemp.replace("has-not-buyer", "has-buyer");
        }
        if (item.templateId !== tid) {

            tidSame = false;
        }
        innerHtml += htmlTemp;
    }
    if (tidSame) {
        document.getElementById("template-id").value = tid;
    } else {
        document.getElementById("template-id").value = "";
    }

    resultBody.innerHTML = innerHtml;
}

