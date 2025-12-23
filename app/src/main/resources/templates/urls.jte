@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
content = @`
    @if(page.getMessageRecord() != null)
        @if(page.getMessageRecord().getId() == 1)
            <div class="rounded-0 m-0 alert alert-dismissible fade show alert-success" role="alert">
                <p class="m-0">${page.getMessageRecord().getMessage()}</p>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif
        @if(page.getMessageRecord().getId() == 2)
            <div class="rounded-0 m-0 alert alert-dismissible fade show alert-info" role="alert">
                <p class="m-0">${page.getMessageRecord().getMessage()}</p>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif
    @endif
    <div class="container-lg mt-5">
        <h1>Сайты</h1>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th>Имя</th>
                <th class="col-2">Последняя проверка</th>
                <th class="col-1">Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @for (var urlData : page.getData())
               <tr>
                   <td>${urlData.getId()}</td>
                   <td><a href="${NamedRoutes.urlDataPath(urlData.getId())}">${urlData.getNameUrl()}</a></td>
                   <td>${String.valueOf(urlData.getLastCheck())}</td>
               </tr>
            @endfor
            </tbody>
        </table>
    </div>


`
)
