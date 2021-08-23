function saveUser() {
    let selfId = document.getElementById("selfId").value;
    let key = document.getElementById("key").value;
    let temporaryLicense = document.getElementById("temporary-license").value;
    axios.get("user/saveUser", {
        params: {
            userId: selfId,
            key: key,
            temporaryLicense: temporaryLicense
        }
    }).then(res => {
        // 接口数据
        let data = res.data;
        console.log(data);
        if (!processErrorResult(data)) {
            return;
        }

        let success = data.result;
        if (success) {
            alert("成功");
        } else {
            alert("失败")
        }
    });
}

function createTemporaryLicense() {
    let temporaryLicenseExpire = document.getElementById("temporary-license-expire").value;
    let concurrentTime = document.getElementById("concurrent-time").value;
    axios.get("user/createTemporaryLicense", {
        params: {
            expireTime: temporaryLicenseExpire,
            concurrentTime: concurrentTime
        }
    }).then(res => {
        // 接口数据
        let data = res.data;
        let success = processErrorResult(data);
        if (!success) {
            return;
        }
        console.log(data);
        alert(data.result.message);
    });
}

function deleteTemporaryLicense() {
    let temporaryLicense = document.getElementById("temporary-license").value;
    axios.get("user/deleteTemporaryLicense", {
        params: {
            temporaryLicense: temporaryLicense,
        }
    }).then(res => {
        // 接口数据
        let data = res.data;
        let success = processErrorResult(data);
        if (!success) {
            return;
        }
        console.log(data);
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

    axios.get("template/saveTemplate", {
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
    let buyerId = document.getElementById("buyerId").value;

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

    axios.get("auction/getAuctionItems", {
        params: {
            page: page,
            order: order,
            sort: sort,
            itemName: itemName,
            userId: userId,
            selfId: selfId,
            buyerId: buyerId
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
        return;
    }

    document.getElementById("result-body").innerHTML = "";

    axios.get("auction/getAuctionPriceOder", {
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

function buildTableHtml(item, i) {
    let bidUnitColor = "bid-unit-black";
    let mouthUnitColor = "mouth-unit-black";
    let buyStatus = "has-not-buyer";
    let userDefinePrice = "";

    if (item == null) {
        item = new Item();
    } else {
        item.unitPrice = parseFloat(item.unitPrice.toFixed(2));
        item.unitMouthfulPrice = parseFloat(item.unitMouthfulPrice.toFixed(2));

        if (item.userDefinePrice != null) {
            userDefinePrice = item.userDefinePrice;
            if (item.unitPrice > 0 && item.userDefinePrice > item.unitPrice) {
                bidUnitColor = "bid-unit-red";
            }

            if (item.unitMouthfulPrice > 0 && item.userDefinePrice > item.unitMouthfulPrice) {
                mouthUnitColor = "mouth-unit-red";
            }
        }

        if (item.buyerName != null && item.buyerName !== "") {
            buyStatus = "has-buyer";
        }
    }

    return `
            <tr>\n
                <th>${i}</th>
                <th class="table-item-name ${buyStatus}">${item.templateName}</th>
                <th>${item.count}</th>
                <th>${item.price}</th>
                <th>${item.mouthful}</th>
                <th class="${bidUnitColor}">${item.unitPrice}</th>
                <th class="${mouthUnitColor}">${item.unitMouthfulPrice}</th>
                <th class="expected-price">${userDefinePrice}</th>
                <th class="table-seller">${item.auctioneerName}</th>
                <th class="table-time">${item.beginTimeString}</th>
                <th>${item.validDate}</th>
                <th>${item.templateId}</th>
                <th>${item.pic}</th>
                <th>${item.buyerName}</th>
            </tr>`;
}

function buildTable(items) {
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
        if (item.templateId !== tid) {
            tidSame = false;
        }
        innerHtml += buildTableHtml(item, i);
    }
    if (tidSame) {
        document.getElementById("template-id").value = tid;
    } else {
        document.getElementById("template-id").value = "";
    }

    resultBody.innerHTML = innerHtml;
}
