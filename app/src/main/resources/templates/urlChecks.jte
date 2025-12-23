@import hexlet.code.dto.urls.UrlDataCheckPage
@import hexlet.code.util.NamedRoutes
@param UrlDataCheckPage page

@template.layout.page(
content = @`
    <div class="container-lg mt-5">
        <h1>Сайт: ${page.getName()}</h1>

        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>ID</td>
                <td>${page.getId()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${page.getName()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${String.valueOf(page.getCreatedAt())}</td>
            </tr>
            </tbody>
        </table>
            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="${NamedRoutes.checkUrlPath(page.getId())}">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>
        <table class = "table table-bordered table-hover mt-3">
            <thead>
            <tr><th>ID</th>
                <th>Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th>Дата проверки</th>
            </tr></thead>
            <tbody>
            @for (var check : page.getChecks())
               <tr>
                   <td>${check.getId()}</td>
                   <td>${check.getCodeAnswer()}</td>
                   <td>${check.getTitle()}</td>
                   <td>${check.getH1()}</td>
                   <td>${check.getDescription()}</td>
                   <td>${String.valueOf(check.getDateCheck())}</td>
               </tr>
            @endfor
            </tbody>
        </table>
    </div>


`
)
