import React from "react";
import useNewsSocket from "../hooks/useNewsSocket";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from "chart.js";
import { Line } from "react-chartjs-2";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

export default function MarketPage() {
  const { news, quotes } = useNewsSocket();

  return (
    <div className="p-4 grid grid-cols-2 gap-4">
      {/* Новости */}
      <div className="bg-white p-4 rounded shadow h-[80vh] overflow-y-auto">
        <h2 className="text-xl font-bold mb-4">Финансовые новости</h2>
        <ul className="space-y-4">
          {news.length > 0 ? (
            news.map((newsItem, index) => (
              <li key={index} className="border-b pb-4">
                <h3 className="font-semibold text-lg">{newsItem.title}</h3>
                <a
                  href={newsItem.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-500 hover:underline"
                >
                  Читать статью
                </a>
              </li>
            ))
          ) : (
            <li className="text-center">Нет доступных новостей</li>
          )}
        </ul>
      </div>

      {/* Котировки и графики */}
{/*       <div className="bg-white p-4 rounded shadow h-[80vh] overflow-y-auto"> */}
{/*         <h2 className="text-xl font-bold mb-4">Котировки</h2> */}

{/*         {quotes.length > 0 ? ( */}
{/*           quotes.map((quote) => { */}
{/*             // Сортируем данные по времени */}
{/*             const sortedData = [...quote.data].sort( */}
{/*               (a, b) => new Date(a.timestamp) - new Date(b.timestamp) */}
{/*             ); */}

{/*             // Метки времени на оси X */}
{/*             const labels = sortedData.map((point) => */}
{/*               new Date(point.timestamp).toLocaleTimeString([], { */}
{/*                 hour: "2-digit", */}
{/*                 minute: "2-digit", */}
{/*               }) */}
{/*             ); */}

{/*             // Данные цены закрытия */}
{/*             const data = { */}
{/*               labels, */}
{/*               datasets: [ */}
{/*                 { */}
{/*                   label: `${quote.symbol} — Цена закрытия`, */}
{/*                   data: sortedData.map((point) => point.close), */}
{/*                   borderColor: "rgba(75,192,192,1)", */}
{/*                   backgroundColor: "rgba(75,192,192,0.2)", */}
{/*                   fill: true, */}
{/*                   tension: 0.3, */}
{/*                 }, */}
{/*               ], */}
{/*             }; */}

{/*             const options = { */}
{/*               responsive: true, */}
{/*               plugins: { */}
{/*                 legend: { position: "top" }, */}
{/*                 title: { display: true, text: `График цены закрытия: ${quote.symbol}` }, */}
{/*               }, */}
{/*               scales: { */}
{/*                 x: { */}
{/*                   display: true, */}
{/*                   title: { display: true, text: "Время" }, */}
{/*                 }, */}
{/*                 y: { */}
{/*                   display: true, */}
{/*                   title: { display: true, text: "Цена" }, */}
{/*                 }, */}
{/*               }, */}
{/*             }; */}

{/*             return ( */}
{/*               <div key={quote.symbol} className="mb-8 border-b pb-4"> */}
{/*                 <Line options={options} data={data} /> */}
{/*               </div> */}
{/*             ); */}
{/*           }) */}
{/*         ) : ( */}
{/*           <div className="text-center">Нет доступных котировок</div> */}
{/*         )} */}
{/*       </div> */}
    </div>
  );
}
