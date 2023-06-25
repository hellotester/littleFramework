(function waitReq(requestPartialUrl) {
    return performance.getEntriesByType('resource')
            .filter(item => item.initiatorType == 'xmlhttprequest' &&
             item.name.toLowerCase().includes(requestPartialUrl))[0] !== undefined;
})()